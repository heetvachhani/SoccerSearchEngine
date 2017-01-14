package clustering;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.stereotype.Service;
// getting cluster data from another rest service
// used another java app to get cluster result as both code were using different lucene version and it was creating problem
@Service
public class ClusteringRestConnection {

	static String baseUrl = "http://localhost:8080/";

	public InputStream getConnection(String action, String requestMethod) {
		HttpURLConnection urlConnection = null;
		URL url;
		InputStream in = null;
		try {
			url = new URL(baseUrl + action);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod(requestMethod);
			urlConnection.setRequestProperty("Accept", "application/json");
			urlConnection.connect();
			if (urlConnection.getResponseCode() != 200) {
				System.out.println("Failed : HTTP error code : " + urlConnection.getResponseCode());
			}

			in = new BufferedInputStream(urlConnection.getInputStream());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return in;
	}
}
