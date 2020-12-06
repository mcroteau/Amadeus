<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Amadeus: Flyers</title>
</head>

<body>

<h1>Ads</h1>

<c:if test="${not empty message}">
    <div class="notify notify-info">{message}</div>
</c:if>

<a href="/o/flyer/create" class="href-dotted">Start New Ad</a>

<c:if test="${flyers.size() > 0}">

    <c:forEach var="flyer" items="${flyers}">
        <div class="">
            <h3>#${flyer.id}
                <c:if test="${flyer.active}">
                <span class="modern tiny">Running</span>
                </c:if>
            </h3>
            <a href="/o/flyer/edit/${flyer.id}" class="href-dotted">${flyer.pageUri}</a>
            <br/>
            <c:if test="${!flyer.active}">
                <a href="/o/flyer/staging/${flyer.id}" title="" class="button retro">Go Live</a>
            </c:if>
        </div>
    </c:forEach>

</c:if>
<c:if test="${flyers.size() == 0}">
    <p>No ads created yet.</p>
</c:if>

</body>
</html>