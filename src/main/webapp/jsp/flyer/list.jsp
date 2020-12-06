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

<a href="/o/flyer/create" class="href-dotted">Start Ad</a>

<c:if test="${flyers.size() > 0}">

    <table class="table table-condensed">
        <c:forEach var="flyer" items="${flyers}">
            <tr>
                <td>${flyer.id}</td>
                <td>
                    <c:if test="${not empty flyer.imageUri}">
                        <img src="/o/${flyer.imageUri}" style="width:100px;">
                    </c:if>
                </td>
                <td>${flyer.pageUri}</td>
                <td>${flyer.description}</td>
                <td>
                    <c:if test="${flyer.active}">
                        <span class="retro">Running</span>
                    </c:if>
                </td>
                <td>
                    <c:if test="${!flyer.active}">
                        <a href="/o/flyer/zula/${flyer.id}" title="" class="button retro">Go Live</a>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
    </table>
</c:if>
<c:if test="${flyers.size() == 0}">
    <p>No ads created yet.</p>
</c:if>

</body>
</html>