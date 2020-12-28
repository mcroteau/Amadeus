<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h1>Handouts</h1>

<a href="/o/sheet/create" class="href-dotted">New Handout</a>

<p style="font-size:19px;line-height: 1.2em">What are handouts? Handouts are a representation of either a business, a place, an organization or an event.</p>

<c:if test="${not empty message}">
    <div class="notify notify-info">{message}</div>
</c:if>


<c:if test="${sheets.size() > 0}">
    <table style="width:100%;margin-top:30px;">
    <c:forEach var="sheet" items="${sheets}">
        <tr>
            <td>
                <c:if test="${not empty sheet.imageUri}">
                    <img src="${sheet.imageUri}" style="width:60px;border-radius:9px;"/>
                </c:if>
            </td>
            <td>
                <h3>#${sheet.id}</h3>
            </td>
            <td>
                <a href="/o/sheet/edit/${sheet.id}" class="href-dotted">${sheet.title}</a>
            </td>
            <td>
                <span><strong>${sheet.sheetViews}</strong><br/> views</span>
            </td>
        </tr>
    </c:forEach>
    </table>
</c:if>
<c:if test="${sheets.size() == 0}">
    <p>No handouts created yet.</p>
</c:if>
