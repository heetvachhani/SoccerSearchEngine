package queryExpansion;

public class DocResult {

	private String docContents;
	private String urlOfDoc;
	private String titleOfDoc;
	private float hitScore;
	private int clusterID;
	private float rankScore;
	
	public String getDocContents() {
		return docContents;
	}

	public void setDocContents(String docContents) {
		this.docContents = docContents;
	}

	public String getUrlOfDoc() {
		return urlOfDoc;
	}

	public void setUrlOfDoc(String urlOfDoc) {
		this.urlOfDoc = urlOfDoc;
	}

	public String getTitleOfDoc() {
		return titleOfDoc;
	}

	public void setTitleOfDoc(String titleOfDoc) {
		this.titleOfDoc = titleOfDoc;
	}
	
	public void setHitScore(float hitScore) {
		this.hitScore = hitScore;
	}
	
	public float getHitScore() {
		return hitScore;
	}
	
	public void setRankScore(float rankScore) {
		this.rankScore = rankScore;
	}
	
	public float getRankScore() {
		return rankScore;
	}
	
	public void setClusterID(int clusterID){
		this.clusterID = clusterID;
	}
	
	public int getClusterID(){
		return clusterID;
	}
}