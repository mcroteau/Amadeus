<html>
<head>
    <script type="text/javascript" src="/o/js/packages/paper.full.js"></script>
    <style>
        #space{
            height:290px;
            width:100%;
        }
    </style>
</head>
<body>
<canvas id="space" resize></canvas>

<script type="text/paperscript" canvas="space">
    // The amount of symbol we want to place;
    var count = 150;

    var path = new Path.RegularPolygon(new Point(0, 0), 3, 7);

    path.fillColor = '#fff';
    path.strokeColor = '#9eaab7';
    path.strokeWidth = 1;
    path.selected = true;

    var symbol = new SymbolDefinition(path);

    // Place the instances of the symbol:
    for (var i = 0; i < count; i++) {
        // The center position is a random point in the view:
        var center = Point.random() * view.size;
        var placed = symbol.place(center);
        var scale = (i + 1) / count;
        placed.scale(scale);
        placed.data.vector = new Point({
            angle: Math.random() * 360,
            length : scale * Math.random() / 5
        });
    }

    var vector = new Point({
        angle: 45,
        length: 0
    });

    var mouseVector = vector.clone();

    // The onFrame function is called up to 60 times a second:
    function onFrame(event) {
        vector = vector + (mouseVector - vector) / 30;

        // Run through the active layer's children list and change
        // the position of the placed symbols:
        for (var i = 0; i < count; i++) {
            var item = project.activeLayer.children[i];
            var size = item.bounds.size;
            var length = vector.length / 10 * size.width / 10;
            item.position += vector.normalize(length) + item.data.vector + 0.1;
            keepInView(item);
        }
    }

    function keepInView(item) {
        var position = item.position;
        var itemBounds = item.bounds;
        var bounds = view.bounds;
        if (itemBounds.left > bounds.width) {
            position.x = -item.bounds.width;
        }

        if (position.x < -itemBounds.width) {
            position.x = bounds.width + itemBounds.width;
        }

        if (itemBounds.top > view.size.height) {
            position.y = -itemBounds.height;
        }

        if (position.y < -itemBounds.height) {
            position.y = bounds.height  + itemBounds.height / 2;
        }
    }
</script>
</body>
</html>
