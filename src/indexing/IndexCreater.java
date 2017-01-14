package indexing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import queryExpansion.DocResult;
import queryExpansion.QueryProcessor;

/**
 * @author heetvachhani
 *
 */
public class IndexCreater {

	public static Analyzer analyzer = new SnowballAnalyzer(Version.LUCENE_40, "English");
	private IndexWriter writer;
	public static String indexLocation = "/Users/Heet/Downloads/soccerData/indexInfo/index_snow.fl";
	String docLocation = "/Users/Heet/Downloads/soccerData/documents";

	public void readFiles() throws IOException {
		File[] listOfFiles = new File(docLocation).listFiles();
		for (File file : listOfFiles) {
			indexFileOrDirectory(file.getAbsolutePath());
			System.out.println(file.getName());
		}
		closeIndex();
	}

	public IndexCreater(String indexDir) throws IOException {
		FSDirectory dir = FSDirectory.open(new File(indexDir));
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, analyzer);
		writer = new IndexWriter(dir, config);
	}

	public static void main(String[] args) throws Exception {
		 IndexCreater index = new IndexCreater(IndexCreater.indexLocation);
		 index.readFiles();
//		ArrayList<DocResult> res = QueryProcessor.processQuery("messi");
//		for(DocResult dr: res){
//			System.out.println(dr.getUrlOfDoc());
//			System.out.println(dr.getTitleOfDoc());
//			System.out.println("================");
//		}
	}

	public void indexFileOrDirectory(String fileName) throws IOException {
		String line = "";
		String url = "";
		String title = "";
		FileReader fr = new FileReader(fileName);
		StringBuilder str = new StringBuilder();
		BufferedReader bufferedReader = new BufferedReader(fr);
		while ((line = bufferedReader.readLine()) != null) {
			if (line.contains("Title:")) {
				title = line.replaceAll("Title:", "").replace("::", "");
				str.append(title);
			} else if (line.contains("URL:")) {
				url = line.replaceAll("URL:", "").replace("::", "");
			} else if(!line.contains("Outlinks:")){
				str.append(line);
			}
		}
		File file = new File(fileName);
		try {
			Document doc = new Document();
			Field f = new Field("content", str.toString(), Field.Store.YES, Field.Index.ANALYZED,
					Field.TermVector.WITH_POSITIONS_OFFSETS);
			f.setBoost(2.0f);
			doc.add(f);
			doc.add(new StringField("url", url, Field.Store.YES));
			Field titleF = new Field("title", title, Field.Store.YES, Field.Index.ANALYZED,
					Field.TermVector.WITH_POSITIONS_OFFSETS);
			titleF.setBoost(3.0f);
			doc.add(titleF);
			doc.add(new StringField("filename", file.getName(), Field.Store.YES));
			writer.addDocument(doc);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			fr.close();
		}
	}

	public void closeIndex() throws IOException {
		writer.close();
	}

}