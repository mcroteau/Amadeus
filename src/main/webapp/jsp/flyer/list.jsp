<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h1>Ads</h1>

<c:if test="${not empty message}">
    <div class="notify notify-info">{message}</div>
</c:if>

<a href="/o/flyer/create" class="href-dotted">New Ad</a>

<c:if test="${flyers.size() > 0}">
    <table style="width:100%;margin-top:30px;">
    <c:forEach var="flyer" items="${flyers}">
        <tr>
            <td>
                <c:if test="${not empty flyer.imageUri}">
                    <img src="/o/${flyer.imageUri}" style="width:100px;"/>
                </c:if>
            </td>
            <td>
                <h3>#${flyer.id}</h3>
            </td>
            <td>
                <a href="/o/flyer/edit/${flyer.id}" class="href-dotted">${flyer.pageUri}</a>
            </td>
            <td>
                <c:if test="${flyer.active}">
                    <span class="yella">Running</span>
                </c:if>
            </td>
            <td>
                <c:if test="${!flyer.active}">
                    <a href="/o/flyer/staging/${flyer.id}" title="" class="button retro">Go Live</a>
                </c:if>
            </td>
        </tr>
    </c:forEach>
    </table>
</c:if>
<c:if test="${flyers.size() == 0}">
    <p>No ads created yet.</p>
</c:if>
