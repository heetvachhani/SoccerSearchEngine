package clustering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.apporiented.algorithm.clustering.AverageLinkageStrategy;
import com.apporiented.algorithm.clustering.SingleLinkageStrategy;
import com.apporiented.algorithm.clustering.WeightedLinkageStrategy;
import com.apporiented.algorithm.clustering.visualization.DendrogramPanel;
import com.apporiented.algorithm.clustering.Cluster;
import com.apporiented.algorithm.clustering.ClusteringAlgorithm;
import com.apporiented.algorithm.clustering.CompleteLinkageStrategy;
import com.apporiented.algorithm.clustering.PDistClusteringAlgorithm;

import queryExpansion.DocResult;

public class HeirarClustering {
	static String[] inputArray;
	static double[][] pdist;
	static HashMap<String, Integer> inputMap = new HashMap<>();
	public static ArrayList<ArrayList<String>> docWordsList = new ArrayList<ArrayList<String>>();
	public static ArrayList<String> totalWordsInAllDocs = new ArrayList<String>();

	public static void main(String[] args) throws FileNotFoundException {
		getAllExamCluster("/Users/Heet/Downloads/docs");
	}

	public static void getAllExamCluster(String ipDocs) throws FileNotFoundException {
		File[] files = new File(ipDocs).listFiles();
		ArrayList<String> global = new ArrayList<String>();
		ArrayList<String[]> docs = new ArrayList<String[]>();
		Set<String> stopwordsSet = new HashSet<String>();

		Scanner stopwordsFile = new Scanner(new File("/Users/Heet/Downloads/stopwords"));
		while (stopwordsFile.hasNext())
			stopwordsSet.add(stopwordsFile.next());
		stopwordsFile.close();

		BufferedReader in;
		File folder = new File("/Users/Heet/Downloads/docs");
		for (File file : folder.listFiles()) {
			in = new BufferedReader(new FileReader(file));
			String line = null;
			ArrayList<String> docWords = new ArrayList<String>();
			try {
				while ((line = in.readLine()) != null) {
					String[] words = line.toLowerCase().split(" ");
					for (String word : words) {
						word = word.replace("'s", "");
						word = word.replaceAll("[^a-z]", "").trim();
						if (word.trim().isEmpty())
							continue;
						if (stopwordsSet.contains(word))
							continue;
						docWords.add(word);

						if (!totalWordsInAllDocs.contains(word))
							totalWordsInAllDocs.add(word);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			docWordsList.add(docWords);
		}

		ArrayList<double[]> docVectors = new ArrayList<double[]>();
		for (ArrayList<String> documentWord : docWordsList) {
			double[] docVector = new double[totalWordsInAllDocs.size()];
			for (int i = 0; i < totalWordsInAllDocs.size(); i++)
				docVector[i] = tf(documentWord, totalWordsInAllDocs.get(i)) * idf(i);
			for (double d : docVector) {
				System.out.print(d + " ");
			}
			System.out.println("\n");
			docVectors.add(docVector);
		}
		ArrayList<Double> cosineSimilarities = new ArrayList<Double>();
		for (int i = 0; i < docVectors.size(); i++) {
			for (int j = i + 1; j < docVectors.size(); j++) {
				double[] first = docVectors.get(i);
				double[] second = docVectors.get(j);
				cosineSimilarities.add(cosineSim(first, second));
			}
		}

		pdist = new double[1][cosineSimilarities.size()];
		for (int i = 0; i < cosineSimilarities.size(); i++) {
			pdist[0][i] = cosineSimilarities.get(i);
			System.out.println(pdist[0][i]);
		}

		String[] names = new String[] { "D1", "D2", "D3", "D4", "D5", "D6", "D7", "D8" };
		ClusteringAlgorithm alg = new PDistClusteringAlgorithm();
		Cluster cluster = alg.performClustering(pdist, names, new AverageLinkageStrategy());

		cluster.toConsole(0);
		System.out.println(cluster.getTotalDistance());
		DendrogramPanel dp = new DendrogramPanel();
		dp.setModel(cluster);

	}

	static double tf(ArrayList<String> docWords, String term) {
		double count = 0;
		for (String s : docWords)
			if (s.equalsIgnoreCase(term))
				count++;
		return count / docWords.size();
	}

	static double idf(int term) {
		double count = 0;
		for (ArrayList<String> x : docWordsList)
			for (String s : x)
				if (s.equalsIgnoreCase(totalWordsInAllDocs.get(term))) {
					count++;
					break;
				}
		return Math.log(docWordsList.size() / count);
	}

	public static ArrayList<DocResult> getAverageLinkageCluster(ArrayList<DocResult> inputResults) {
		ArrayList<DocResult> clusterResult = new ArrayList<>();
		ArrayList<String[]> docs = new ArrayList<String[]>();
		ArrayList<String> global = new ArrayList<String>();
		ArrayList<String> inputs = new ArrayList<String>();

		for (int i = 0; i < inputResults.size() && i < 20; i++) {
			StringBuffer sb = new StringBuffer();
			DocResult document = new DocResult();
			document = inputResults.get(i);

			String url = document.getUrlOfDoc();
			inputMap.put(url, i);
			inputs.add(url + ", " + document.getDocContents());
			sb.append(document.getDocContents());

			// input cleaning regex
			String[] d = sb.toString().toLowerCase().replaceAll("[\\W&&[^\\s]]", "").replaceAll("[^a-zA-Z\\s]", "")
					.replaceAll("\\s+", " ").split("\\W+");
			for (String u : d)
				if (!global.contains(u))
					global.add(u);
			docs.add(d);
		}
		//

		// compute tf-idf and create document vectors (double[])
		ArrayList<double[]> vecspace = new ArrayList<double[]>();
		for (String[] s : docs) {
			double[] d = new double[global.size()];
			for (int i = 0; i < global.size(); i++)
				d[i] = tf(s, global.get(i)) * idf(docs, global.get(i));
			vecspace.add(d);
		}

		ArrayList<Double> cosineSimilarities = new ArrayList<Double>();
		for (int i = 0; i < vecspace.size(); i++) {
			for (int j = i + 1; j < vecspace.size(); j++) {
				double[] first = vecspace.get(i);
				double[] second = vecspace.get(j);
				cosineSimilarities.add(cosineSim(first, second));
			}
		}
		inputArray = new String[inputs.size()];
		for (int i = 0; i < inputs.size(); i++) {
			inputArray[i] = inputs.get(i);
		}
		pdist = new double[1][cosineSimilarities.size()];
		for (int i = 0; i < cosineSimilarities.size(); i++) {
			pdist[0][i] = cosineSimilarities.get(i);
		}
		ClusteringAlgorithm alg = new PDistClusteringAlgorithm();
		Cluster cluster = alg.performClustering((double[][]) pdist, inputArray, new AverageLinkageStrategy());
		System.out.println("Heirrach......==========");
		System.out.println(cluster.getLeafNames());
		List<String> result = cluster.getLeafNames();
		int id = 0;
		for (String res : result) {
			id = inputMap.get(res.split(", ")[0]);
			System.out.println(id);
			System.out.println(inputResults.get(id).getUrlOfDoc());
			clusterResult.add(inputResults.get(id));
		}
		return clusterResult;
	}

	public static ArrayList<DocResult> getSingleLinkageCluster(ArrayList<DocResult> inputResults) {
		ClusteringAlgorithm alg = new PDistClusteringAlgorithm();
		Cluster cluster = alg.performClustering((double[][]) pdist, inputArray, new SingleLinkageStrategy());
		ArrayList<DocResult> clusterResult = new ArrayList<>();

		System.out.println("Single linkage......==========");
		System.out.println(cluster.getLeafNames());
		List<String> result = cluster.getLeafNames();
		int id = 0;
		for (String res : result) {
			id = inputMap.get(res.split(", ")[0]);
			System.out.println(id);
			System.out.println(inputResults.get(id).getUrlOfDoc());
			clusterResult.add(inputResults.get(id));
		}
		return clusterResult;
	}

	public static ArrayList<DocResult> getWeightedLinkageCluster(ArrayList<DocResult> inputResults) {
		ClusteringAlgorithm alg = new PDistClusteringAlgorithm();
		Cluster cluster = alg.performClustering((double[][]) pdist, inputArray, new WeightedLinkageStrategy());
		ArrayList<DocResult> clusterResult = new ArrayList<>();

		System.out.println("Weighted linkage......==========");
		System.out.println(cluster.getLeafNames());
		List<String> result = cluster.getLeafNames();
		int id = 0;

		for (String res : result) {
			id = inputMap.get(res.split(", ")[0]);

			clusterResult.add(inputResults.get(id));
		}
		return clusterResult;
	}

	public static ArrayList<DocResult> getCompleteLinkageCluster(ArrayList<DocResult> inputResults) {
		ClusteringAlgorithm alg = new PDistClusteringAlgorithm();
		Cluster cluster = alg.performClustering((double[][]) pdist, inputArray, new CompleteLinkageStrategy());
		ArrayList<DocResult> clusterResult = new ArrayList<>();

		System.out.println("Complete linkage......==========");
		System.out.println(cluster.getLeafNames());
		List<String> result = cluster.getLeafNames();
		int id = 0;
		ArrayList<Float> ranks = new ArrayList<>();
		for (int i = 0; i < result.size(); i++) {
			ranks.add((float) Math.random());
		}
		Collections.sort(ranks);
		int i = 0;

		for (String res : result) {
			id = inputMap.get(res.split(", ")[0]);
			// System.out.println(id);
			// System.out.println(inputResults.get(id).getUrlOfDoc());
			DocResult tempDoc = inputResults.get(id);
			tempDoc.setRankScore(1 - ranks.get(i++));
			clusterResult.add(tempDoc);
		}
		return clusterResult;
	}

	static double cosineSim(double[] docVector1, double[] docVector2) {
		double dProduct = 0, magOfA = 0, magOfB = 0;
		for (int i = 0; i < docVector1.length; i++) {
			dProduct += docVector1[i] * docVector2[i];
			magOfA += Math.pow(docVector1[i], 2);
			magOfB += Math.pow(docVector2[i], 2);
		}
		magOfA = Math.sqrt(magOfA);
		magOfB = Math.sqrt(magOfB);
		if ((magOfA != 0) || (magOfB != 0))
			return dProduct / (magOfA * magOfB);
		else
			return 0.0;
	}

	static double tf(String[] doc, String term) {
		double n = 0;
		for (String s : doc)
			if (s.equalsIgnoreCase(term))
				n++;
		return n / doc.length;
	}

	static double idf(ArrayList<String[]> docs, String term) {
		double n = 0;
		for (String[] x : docs)
			for (String s : x)
				if (s.equalsIgnoreCase(term)) {
					n++;
					break;
				}
		return Math.log(docs.size() / n);
	}

}
