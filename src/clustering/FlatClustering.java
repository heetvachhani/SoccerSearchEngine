package clustering;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import queryExpansion.DocResult;

public class FlatClustering {

	public static ArrayList<DocResult> getFlatCluster(ArrayList<DocResult> inputResults) {
		ArrayList<DocResult> clusterResult = new ArrayList<>();
		ArrayList<String[]> docs = new ArrayList<String[]>();
		ArrayList<String> filenames = new ArrayList<String>();
		ArrayList<String> global = new ArrayList<String>();
		HashMap<String, Integer> inputMap = new HashMap<>();
		HashMap<String,Float> rankMap = new HashMap<>();
		for (int i = 0; i < inputResults.size() && i < 30; i++) {
			StringBuffer sb = new StringBuffer();
			DocResult document = new DocResult();
			document = inputResults.get(i);
			inputMap.put(document.getUrlOfDoc(), i);
			rankMap.put(document.getUrlOfDoc(), document.getHitScore());
			sb.append(document.getDocContents());
			// input cleaning regex
			String[] d = sb.toString().toLowerCase().replaceAll("[\\W&&[^\\s]]", "").replaceAll("[^a-zA-Z\\s]", "")
					.replaceAll("\\s+", " ").split("\\W+");
			for (String u : d)
				if (!global.contains(u))
					global.add(u);
			docs.add(d);
			filenames.add(document.getUrlOfDoc());
		}

		ArrayList<double[]> vecspace = new ArrayList<double[]>();
		for (String[] s : docs) {
			double[] d = new double[global.size()];
			for (int i = 0; i < global.size(); i++)
				d[i] = tf(s, global.get(i)) * idf(docs, global.get(i));
			vecspace.add(d);
		}

		// iterate k-means
		HashMap<double[], TreeSet<Integer>> clusters = new HashMap<double[], TreeSet<Integer>>();
		HashMap<double[], TreeSet<Integer>> step = new HashMap<double[], TreeSet<Integer>>();
		HashSet<Integer> rand = new HashSet<Integer>();
		TreeMap<Double, HashMap<double[], TreeSet<Integer>>> errorsums = new TreeMap<Double, HashMap<double[], TreeSet<Integer>>>();
		int k = 4;
		int maxiter = 20;
		for (int init = 0; init < 2; init++) {
			clusters.clear();
			step.clear();
			rand.clear();
			// randomly initialize cluster centers
			while (rand.size() < k)
				rand.add((int) (Math.random() * vecspace.size()));
			for (int r : rand) {
				double[] temp = new double[vecspace.get(r).length];
				System.arraycopy(vecspace.get(r), 0, temp, 0, temp.length);
				step.put(temp, new TreeSet<Integer>());
			}
			boolean go = true;
			int iter = 0;
			while (go) {
				clusters = new HashMap<double[], TreeSet<Integer>>(step);
				// cluster assignment step
				for (int i = 0; i < vecspace.size(); i++) {
					double[] cent = null;
					double sim = 0;
					for (double[] c : clusters.keySet()) {
						double csim = cosSim(vecspace.get(i), c);
						if (csim > sim) {
							sim = csim;
							cent = c;
						}
					}
					if (cent != null)
						clusters.get(cent).add(i);
				}
				// centroid update step
				step.clear();
				for (double[] cent : clusters.keySet()) {
					double[] updatec = new double[cent.length];
					for (int d : clusters.get(cent)) {
						double[] doc = vecspace.get(d);
						for (int i = 0; i < updatec.length; i++)
							updatec[i] += doc[i];
					}
					for (int i = 0; i < updatec.length; i++)
						updatec[i] /= clusters.get(cent).size();
					step.put(updatec, new TreeSet<Integer>());
				}
				// check break conditions
				String oldcent = "", newcent = "";
				for (double[] x : clusters.keySet())
					oldcent += Arrays.toString(x);
				for (double[] x : step.keySet())
					newcent += Arrays.toString(x);
				if (oldcent.equals(newcent))
					go = false;
				if (++iter >= maxiter)
					go = false;
			}
			System.out.println(clusters.toString().replaceAll("\\[[\\w@]+=", ""));
			if (iter < maxiter)
				System.out.println("Converged in " + iter + " steps.");
			else
				System.out.println("Stopped after " + maxiter + " iterations.");
			System.out.println("");

			// calculate similarity sum and map it to the clustering
			double sumsim = 0;
			for (double[] c : clusters.keySet()) {
				TreeSet<Integer> cl = clusters.get(c);
				for (int vi : cl) {
					sumsim += cosSim(c, vecspace.get(vi));
				}
			}
			errorsums.put(sumsim, new HashMap<double[], TreeSet<Integer>>(clusters));

		}
		// pick the clustering with the maximum similarity sum and print the
		// filenames and indices
		System.out.println("Best Convergence:");
		System.out.println(errorsums.get(errorsums.lastKey()).toString().replaceAll("\\[[\\w@]+=", ""));
		System.out.print("{");
		int clusterNo = 1;
		
		for (double[] cent : errorsums.get(errorsums.lastKey()).keySet()) {
			System.out.print("[");
			for (int pts : errorsums.get(errorsums.lastKey()).get(cent)) {
				System.out.print(filenames.get(pts).substring(0, filenames.get(pts).length() - 1) + ", ");
				DocResult dc = inputResults.get(inputMap.get(filenames.get(pts)));
				dc.setClusterID(clusterNo);
				clusterResult.add(dc);
				
			}
			clusterNo++;
			System.out.print("\b\b], ");
		}
		System.out.println("\b\b}");
		return clusterResult;

	}

	static double cosSim(double[] a, double[] b) {
		double dotp = 0, maga = 0, magb = 0;
		for (int i = 0; i < a.length; i++) {
			dotp += a[i] * b[i];
			maga += Math.pow(a[i], 2);
			magb += Math.pow(b[i], 2);
		}
		maga = Math.sqrt(maga);
		magb = Math.sqrt(magb);
		double d = dotp / (maga * magb);
		return d == Double.NaN ? 0 : d;
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

	public static ArrayList<DocResult> getKMeansCluster(String query) {

		ClusteringRestConnection conn = new ClusteringRestConnection();
		ArrayList<DocResult> clusterResult = null;
		System.out.println("Sending query to clustering"+query);
		query = query.replaceAll(" ", "%20");
		String url = "company" + "/" + query;
		String outputString = "";
		InputStream in = conn.getConnection(url, "GET");
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			outputString = sb.toString();
			System.out.println("Inside k means======");
			clusterResult = new ArrayList<>();
			JSONArray jsonArray = JSONArray.fromObject(outputString);
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = (JSONObject) jsonArray.get(i);
				DocResult dr = new DocResult();
				dr.setClusterID((int) jsonObject.get("clusterId"));
				dr.setUrlOfDoc(jsonObject.get("url").toString());
				dr.setTitleOfDoc(jsonObject.get("title").toString());
				dr.setDocContents(jsonObject.get("content").toString());
				System.out.println(dr.getClusterID());
				System.out.println(dr.getUrlOfDoc());
				clusterResult.add(dr);
			}

			in.close();
		} catch (Exception e) {
			System.out.println("Buffer Error" + "Error converting result " + e.toString());
		}
		return clusterResult;

	}

}