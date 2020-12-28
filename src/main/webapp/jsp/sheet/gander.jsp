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
        #sheet-content{
            padding:20px 30px 30px;
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
            <svg id="amadeus-logo" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 130 130">
                <path id="amadeus-icon-path" d="M57.46 78.91L36.35 78.87L54.53 30.08L75.83 30.16L68.2 52.86L92 52.85L57.55 115L56.95 115L57.46 78.91ZM57.46 78.91L36.31 78.99L54.62 30.16L75.81 30.07L68.2 52.86L92 52.86L57.55 115L56.95 115L57.46 78.91Z"/>
            </svg>
        </a>
    </div>

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

</body>
</html>
