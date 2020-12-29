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
            <svg id="amadeus-logo" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 70 70" width="70" height="70">
                <g id="amadeus">
                    <path id="Shape 13" class="shp0" d="M46 31L46 36L57.01 36L57.06 40.91L62 41L62 31L46 31Z" />
                    <path id="Shape 10" fill-rule="evenodd" class="shp1" d="M16 20L21 20L32 48L4 48L16 20ZM11 44L25 44L19 26L18 26L11 44Z" />
                </g>
                <path id="Shape 13" fill-rule="evenodd" class="shp0" d="M38.5 23C36.01 23 34 20.99 34 18.5C34 16.01 36.01 14 38.5 14C40.99 14 43 16.01 43 18.5C43 20.99 40.99 23 38.5 23ZM40.6 18.5C40.6 17.34 39.66 16.4 38.5 16.4C37.34 16.4 36.4 17.34 36.4 18.5C36.4 19.66 37.34 20.6 38.5 20.6C39.66 20.6 40.6 19.66 40.6 18.5Z" />
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
