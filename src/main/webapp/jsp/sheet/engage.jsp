<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>${sheet.title}</title>

    <link rel="icon" type="image/png" href="/o/images/icon.png?v=<%=System.currentTimeMillis()%>">

    <script type="text/javascript" src="/o/js/packages/jquery.js"></script>
    <script type="text/javascript" src="/o/js/packages/anchorme.js"></script>

    <link rel="stylesheet" href="/o/css/app.gap.css?v=<%=System.currentTimeMillis()%>"/>
    <link rel="stylesheet" href="/o/css/app.astro.css?v=<%=System.currentTimeMillis()%>"/>

    <style>
        #main-container{
            width:670px;
            margin:30px auto 100px;
            border: solid 1px #eff2f3;
            border-top-left-radius: 20px;
            border-top-right-radius: 20px;
            -webkit-box-shadow: 4px 3px 59px 0px rgba(0,0,0,.23) !important;
            -moz-box-shadow: 4px 3px 59px 0px rgba(0,0,0,.23) !important;
            box-shadow: 4px 3px 59px 0px rgba(0,0,0,.23) !important;
        }
        #main-container.dropitlikeitshotnoshadow{
            box-shadow: none !important;
            border:none;
        }
        #sheet-content{
            line-height: 1.4em;
            padding:20px 50px 80px;
        }
        #sheet-image{
            width:100%;
            height:335px;
            border-top-left-radius: 20px;
            border-top-right-radius: 20px;
        }

    </style>

</head>
<body>

    <div id="logo-container" style="position:absolute;">
        <a href="/o/" id="logo-logo">
            <svg id="amadeus-logo" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 171 171" width="171" height="171">
                <path d="M9.86 154.32L73.49 154.32L39.95 99.29L9 154.32L9.86 154.32Z" />
                <path d="M128.16 152.92L159 152.92L81.9 26.75L80.5 26.75L65.86 53.27L128.16 152.92Z" />
            </svg>
        </a>
    </div>


    <c:if test="${empty sheet}">
        <div id="main-container" class="dropitlikeitshotnoshadow">
            <h1>Folio not found...</h1>
        </div>
    </c:if>

    <c:if test="${not empty sheet}">
        <div id="main-container">

            <div id="image-container">
                <img src="${sheet.imageUri}" id="sheet-image"/>
            </div>

            <div id="sheet-content">
                <h1 class="ref-check">${sheet.title}</h1>

                <div id="sheet-description" class="ref-check">${sheet.description}</div>
            </div>

        </div>

        <script>
            $(document).ready(function(){

                var refs = document.getElementsByClassName("ref-check")
                var refsArr = Array.from(refs)
                refsArr.forEach(function(ref, index){
                    var data = getData(ref);
                    var hyperlinked = anchorme(data)
                    ref.innerHTML = hyperlinked
                })

                function getData(ref){
                    return {
                        input: ref.innerHTML,
                        options: {
                            attributes: {
                                target: "_blank",
                                class: "href-dotted"
                            },
                        }
                    }
                }

            })
        </script>
    </c:if>

</body>
</html>
