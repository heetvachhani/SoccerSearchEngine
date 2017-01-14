<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<link rel="stylesheet"
	href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Welcome to Soccer Search</title>

<style type="text/css">
.main {
	top: 4.5em;
	background: white;
	margin-bottom: 10px;
}

.query {
	width: 400px;
	height: 25px;
	text-align: left;
	overflow-y: hidden;
	overflow-x: scroll;
}

.search {
	width: 100px;
	color: #1a0dab;
}

.searchRes a:link {
	text-decoration: none;
	color: #1a0dab;
	border: 0px;
	-moz-outline-style: none;
}

.searchRes a:visited {
	text-decoration: none;
	color: #609;
	border: 0px;
	-moz-outline-style: none;
}

.searchRes a:hover {
	text-decoration: underline;
	color: #1a0dab;
	border: 0px;
	-moz-outline-style: none;
}

.lSearch {
	position: fixed;
	width: 48%;
	height: 90%;
	maargin-left: 20px;
	padding: 0px 10px;
	padding-left: 20px;
	/* border: 1px solid black; */
	overflow-y: scroll;
	text-align: left;
	overflow-y: scroll /*text-decoration:underline;*/
}

/*.cluster {
	position: fixed;
	width: 25%;
	height: 90%;
	padding: 0px 10px;
	overflow-y: scroll;
	text-align: center;
}
.local {
	position: fixed;
	margin-left: 26%;
	width: 25%;
	height: 90%;
	padding: 0px 10px;
	overflow-y: scroll;
	text-align: center;
} */
.google {
	position: fixed;
	margin-left: 52%;
	width: 25%;
	height: 90%;
	padding: 0px 0px;
	/* border: 1px solid black; */
	overflow-y: scroll;
	/* color:Blue; */
	text-align: left;
	/*text-decoration:underline;*/
}

.bing {
	position: fixed;
	margin-left: 77%;
	width: 23%;
	height: 90%;
	padding: 0px 10px;
	/* border: 1px solid black; */
	overflow-y: scroll;
	/* color:orange; */
	text-align: left;
	/*text-decoration:underline;*/
}

.titleres {
	font-size: 16px;
}

.center-block {
	display: block;
	margin-left: auto;
	margin-right: auto;
}

body {
	font-family: "Helvetica Neue", "Book Antiqua", Palatino, serif;
}
</style>

</head>
<body>
	<div class="container-fluid">
		<div align="left">
			<h1>Soccer Search</h1>
		</div>
		<div align="left" class="main">
			<form class="form-inline" action="search" method="post">
				<div class="form-group">
					<label class="sr-only">Search:</label> <input style="width: 500px"
						class="form-control" type="text" name="searchText"
						value="${searchText}">
				</div>
				<input class="btn btn-primary" type="submit" name="search"
					value="Search">
			</form>
		</div>

		<div class="lSearch">
			<ul class="nav nav-tabs">
				<li class="active"><a data-toggle="tab" href="#hitSearch">Hits
						Search</a></li>
				<li><a data-toggle="tab" href="#pageRank">VSM + Page Rank</a></li>
				<li><a data-toggle="tab" href="#flatCluster">K-Means</a></li>
				<li><a data-toggle="tab" href="#singleCluster">Agglomerative</a></li>
				<li><a data-toggle="tab" href="#avgCluster">Query Expansion</a></li>
		<!-- 		<li><a data-toggle="tab" href="#weightedCluster">Cluster 4</a></li>
 -->
			</ul>
		</div>
		<div class="tab-content searchRes"
			style="position: fixed; width: 48%; height: 90%; margin-top: 40px; overflow-y: scroll; text-align: left;">
			<div id="hitSearch" class="tab-pane fade in active">
				<h3>Hits Search</h3>
				<c:choose>
					<c:when test="${records.size()!=0}">
						<c:forEach items="${records}" var="doc" varStatus="recordIndex">
							<span class="titleres"> <strong><a
									href='<c:out value="${doc.urlOfDoc}"/>'><c:out
											value="${doc.titleOfDoc}" /></a></strong>
							</span><br />
							<a href='<c:out value="${doc.urlOfDoc}"/>'><c:out
									value="${doc.urlOfDoc}" /></a>
							<br>
										${doc.docContents}
										<br />
							<br />
						</c:forEach>
					</c:when>
					<c:otherwise>
						<p>No Result</p>
					</c:otherwise>
				</c:choose>
				<br>
			</div>

			<div id="pageRank" class="searchRes tab-pane fade">
				<h3>VSM + Page Rank</h3>
				<c:choose>
					<c:when test="${completeClusterResult.size() != 0}">

						<c:forEach items="${completeClusterResult}" var="doc"
							varStatus="clusterIndex">
							<span class="titleres"> <strong><a
									href='<c:out value="${doc.urlOfDoc}"/>'><c:out
											value="${doc.titleOfDoc}" /></a></strong>
							</span><br />
							<a href='<c:out value="${doc.urlOfDoc}"/>'><c:out
									value="${doc.urlOfDoc}" /></a>
							<br>
										${doc.docContents}
										<br />
							<br />
						</c:forEach>
					</c:when>
					<c:otherwise>
						<p>No Result</p>
					</c:otherwise>
				</c:choose>
				<br>
			</div>

			<div id="flatCluster" class="searchRes tab-pane fade">
				<h3>K-means Cluster</h3>
				<c:choose>
					<c:when test="${flatClusterResult.size() != 0}">

						<c:forEach items="${flatClusterResult}" var="doc"
							varStatus="clusterIndex">
							<span class="titleres"> <strong><a
									href='<c:out value="${doc.urlOfDoc}"/>'><c:out
											value="${doc.titleOfDoc}" /></a></strong>
							</span>&nbsp; ${doc.clusterID}<br />
							<br />
							<a href='<c:out value="${doc.urlOfDoc}"/>'><c:out
									value="${doc.urlOfDoc}" /></a>
							<br />
										${doc.docContents}
										<br />
							<br />
						</c:forEach>
					</c:when>
					<c:otherwise>

						<p>No Result</p>
					</c:otherwise>
				</c:choose>

			</div>
			
			<div id="singleCluster" class="searchRes tab-pane fade">
				<h3>Agglomerative Cluster</h3>
				<c:choose>
					<c:when test="${singleClusterResult.size() != 0}">

						<c:forEach items="${singleClusterResult}" var="doc"
							varStatus="clusterIndex">
							<span class="titleres"> <strong><a
									href='<c:out value="${doc.urlOfDoc}"/>'><c:out
											value="${doc.titleOfDoc}" /></a></strong>
							</span>
							<br />
							<a href='<c:out value="${doc.urlOfDoc}"/>'><c:out
									value="${doc.urlOfDoc}" /></a>
							<br />
										${doc.docContents}
										<br />
							<br />
						</c:forEach>
					</c:when>
					<c:otherwise>

						<p>No Result</p>
					</c:otherwise>
				</c:choose>
			</div>

			<div id="avgCluster" class="searchRes tab-pane fade">
				<h3>Query Expansion</h3>
<%-- 				<c:choose>
					<c:when test="${avgClusterResult.size() != 0}">
						<c:forEach items="${avgClusterResult}" var="doc"
							varStatus="clusterIndex">
							<span class="titleres"> <strong><a
									href='<c:out value="${doc.urlOfDoc}"/>'><c:out
											value="${doc.titleOfDoc}" /></a></strong>
							</span>
							<br />
							<a href='<c:out value="${doc.urlOfDoc}"/>'><c:out
									value="${doc.urlOfDoc}" /></a>
							<br>
										${doc.docContents}
										<br />
							<br />
						</c:forEach>
					</c:when>
					<c:otherwise>

						<p>No Result</p>
					</c:otherwise>
				</c:choose> --%>
			</div>
			
<%-- 			<div id="weightedCluster" class="searchRes tab-pane fade">
				<h3>Cluster2 Result</h3>
				<c:choose>
					<c:when test="${weightedClusterResult.size() != 0}">

						<c:forEach items="${weightedClusterResult}" var="doc"
							varStatus="clusterIndex">
							<span class="titleres"> <strong><a
									href='<c:out value="${doc.urlOfDoc}"/>'><c:out
											value="${doc.titleOfDoc}" /></a></strong>
							</span>
							<br />
							<a href='<c:out value="${doc.urlOfDoc}"/>'><c:out
									value="${doc.urlOfDoc}" /></a>
							<br>
										${doc.docContents}
										<br />
							<br />
						</c:forEach>
					</c:when>
					<c:otherwise>

						<p>No Result</p>
					</c:otherwise>
				</c:choose>
			</div>--%>
		</div> 
		<!-- <div class="cluster"></div>


	<div class="local"></div>
 -->
		<div class="google searchRes">
			<h3>Google Search</h3>
			<c:choose>
				<c:when test="${googleRecords.size() != 0}">

					<c:forEach items="${googleRecords}" var="doc"
						varStatus="recordIndex">
						<span class="titleres"> <strong><a
								href='<c:out value="${doc.urlOfDoc}"/>'><c:out
										value="${doc.titleOfDoc}" /></a></strong>
						</span>
						<br />
						<a href='<c:out value="${doc.urlOfDoc}"/>'><c:out
								value="${doc.urlOfDoc}" /></a>
						<br>
										${doc.docContents}
										<br />
						<br />
					</c:forEach>
				</c:when>
				<c:otherwise>

					<p>No Result</p>
				</c:otherwise>
			</c:choose>

		</div>

		<div class="bing searchRes">
			<h3>Bing Search</h3>
			<c:choose>
				<c:when test="${bingRecords.size() != 0}">
					<c:forEach items="${bingRecords}" var="doc" varStatus="recordIndex">
						<span class="titleres"> <strong><a
								href='<c:out value="${doc.urlOfDoc}"/>'><c:out
										value="${doc.titleOfDoc}" /></a></strong>
						</span>
						<br />
						<a href='<c:out value="${doc.urlOfDoc}"/>'><c:out
								value="${doc.urlOfDoc}" /></a>
						<br>
										${doc.docContents}
										<br />
						<br />
					</c:forEach>
				</c:when>
				<c:otherwise>
					<p>No Result</p>
				</c:otherwise>
			</c:choose>

		</div>

	</div>
</body>
</html>