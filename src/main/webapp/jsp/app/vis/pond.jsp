<html>
<head>
    <script type="text/javascript" src="/o/js/packages/paper.full.js"></script>
    <style>
        #pond{
            width:100%;
        }
    </style>
</head>
<body>
<canvas id="pond" resize></canvas>

<script type="text/paperscript" canvas="pond">
    // Adapted from Flocking Processing example by Daniel Schiffman:
    // http://processing.org/learning/topics/flocking.html

    var Boid = Base.extend({
        initialize: function(position, maxSpeed, maxForce) {
            var strength = Math.random() * 0.5;
            this.acceleration = new Point();
            this.vector = Point.random() * 2 - 1;
            this.position = position.clone();
            this.radius = 30;
            this.maxSpeed = maxSpeed + strength;
            this.maxForce = maxForce + strength;
            this.amount = strength * 10 + 10;
            this.count = 0;
            this.createItems();
        },

        run: function(boids) {
            this.lastLoc = this.position.clone();
            if (!groupTogether) {
                this.flock(boids);
            } else {
                this.align(boids);
            }
            this.borders();
            this.update();
            this.calculateTail();
            this.moveHead();
        },

        calculateTail: function() {

            var segments = this.path.segments,
                shortSegments = this.shortPath.segments;
            var speed = this.vector.length;
            var pieceLength = 5 + speed / 3;
            var point = this.position;
            segments[0].point = shortSegments[0].point = point;
            // Chain goes the other way than the movement
            var lastVector = -this.vector;
            for (var i = 1; i < this.amount; i++) {
                var vector = segments[i].point - point;
                this.count += speed * 10;
                var wave = Math.sin((this.count + i * 3) / 300);
                var sway = lastVector.rotate(360).normalize(wave);
                point += lastVector.normalize(pieceLength) + sway;
                segments[i].point = point;
                if (i < 3)
                    shortSegments[i].point = point;
                lastVector = vector;
            }
            this.path.smooth();
        },

        createItems: function() {
            this.head = new Shape.Ellipse({
                center: [0, 0],
                size: [13, 8],
                fillColor: 'black'
            });

            this.path = new Path({
                strokeColor: 'black',
                strokeWidth: 2,
                strokeCap: 'round'
            });
            for (var i = 0; i < this.amount; i++)
                this.path.add(new Point());

            this.shortPath = new Path({
                strokeColor: 'black',
                strokeWidth: 4,
                strokeCap: 'round'
            });
            for (var i = 0; i < Math.min(3, this.amount); i++)
                this.shortPath.add(new Point());
        },

        moveHead: function() {
            this.head.position = this.position;
            this.head.rotation = this.vector.angle;
        },

        // We accumulate a new acceleration each time based on three rules
        flock: function(boids) {
            var separation = this.separate(boids) * 3;
            var alignment = this.align(boids);
            var cohesion = this.cohesion(boids);
            this.acceleration += separation + alignment + cohesion;
        },

        update: function() {
            // Update velocity
            this.vector += this.acceleration;
            // Limit speed (vector#limit?)
            this.vector.length = Math.min(this.maxSpeed, this.vector.length);
            this.position += this.vector;
            // Reset acceleration to 0 each cycle
            this.acceleration = new Point();
        },

        seek: function(target) {
            this.acceleration += this.steer(target, false);
        },

        arrive: function(target) {
            this.acceleration += this.steer(target, true);
        },

        borders: function() {
            var vector = new Point();
            var position = this.position;
            var radius = this.radius;
            var size = view.size;
            if (position.x < -radius) vector.x = size.width + radius;
            if (position.y < -radius) vector.y = size.height + radius;
            if (position.x > size.width + radius) vector.x = -size.width -radius;
            if (position.y > size.height + radius) vector.y = -size.height -radius;
            if (!vector.isZero()) {
                this.position += vector;
                var segments = this.path.segments;
                for (var i = 0; i < this.amount; i++) {
                    segments[i].point += vector;
                }
            }
        },

        // A method that calculates a steering vector towards a target
        // Takes a second argument, if true, it slows down as it approaches
        // the target
        steer: function(target, slowdown) {
            var steer,
                desired = target - this.position;
            var distance = desired.length;
            // Two options for desired vector magnitude
            // (1 -- based on distance, 2 -- maxSpeed)
            if (slowdown && distance < 100) {
                // This damping is somewhat arbitrary:
                desired.length = this.maxSpeed * (distance / 100);
            } else {
                desired.length = this.maxSpeed;
            }
            steer = desired - this.vector;
            steer.length = Math.min(this.maxForce, steer.length);
            return steer;
        },

        separate: function(boids) {
            var desiredSeperation = 60;
            var steer = new Point();
            var count = 0;
            // For every boid in the system, check if it's too close
            for (var i = 0, l = boids.length; i < l; i++) {
                var other = boids[i];
                var vector = this.position - other.position;
                var distance = vector.length;
                if (distance > 0 && distance < desiredSeperation) {
                    // Calculate vector pointing away from neighbor
                    steer += vector.normalize(1 / distance);
                    count++;
                }
            }
            // Average -- divide by how many
            if (count > 0)
                steer /= count;
            if (!steer.isZero()) {
                // Implement Reynolds: Steering = Desired - Velocity
                steer.length = this.maxSpeed;
                steer -= this.vector;
                steer.length = Math.min(steer.length, this.maxForce);
            }
            return steer;
        },

        // Alignment
        // For every nearby boid in the system, calculate the average velocity
        align: function(boids) {
            var neighborDist = 25;
            var steer = new Point();
            var count = 0;
            for (var i = 0, l = boids.length; i < l; i++) {
                var other = boids[i];
                var distance = this.position.getDistance(other.position);
                if (distance > 0 && distance < neighborDist) {
                    steer += other.vector;
                    count++;
                }
            }

            if (count > 0)
                steer /= count;
            if (!steer.isZero()) {
                // Implement Reynolds: Steering = Desired - Velocity
                steer.length = this.maxSpeed;
                steer -= this.vector;
                steer.length = Math.min(steer.length, this.maxForce);
            }
            return steer;
        },

        // Cohesion
        // For the average location (i.e. center) of all nearby boids,
        // calculate steering vector towards that location
        cohesion: function(boids) {
            var neighborDist = 100;
            var sum = new Point();
            var count = 0;
            for (var i = 0, l = boids.length; i < l; i++) {
                var other = boids[i];
                var distance = this.position.getDistance(other.position);
                if (distance > 0 && distance < neighborDist) {
                    sum += other.position; // Add location
                    count++;
                }
            }
            if (count > 0) {
                sum /= count;
                // Steer towards the location
                return this.steer(sum, false);
            }
            return sum;
        }
    });

    var heartPath = new Path('M514.69629,624.70313c-7.10205,-27.02441 -17.2373,-52.39453 -30.40576,-76.10059c-13.17383,-23.70703 -38.65137,-60.52246 -76.44434,-110.45801c-27.71631,-36.64355 -44.78174,-59.89355 -51.19189,-69.74414c-10.5376,-16.02979 -18.15527,-30.74951 -22.84717,-44.14893c-4.69727,-13.39893 -7.04297,-26.97021 -7.04297,-40.71289c0,-25.42432 8.47119,-46.72559 25.42383,-63.90381c16.94775,-17.17871 37.90527,-25.76758 62.87354,-25.76758c25.19287,0 47.06885,8.93262 65.62158,26.79834c13.96826,13.28662 25.30615,33.10059 34.01318,59.4375c7.55859,-25.88037 18.20898,-45.57666 31.95215,-59.09424c19.00879,-18.32178 40.99707,-27.48535 65.96484,-27.48535c24.7373,0 45.69531,8.53564 62.87305,25.5957c17.17871,17.06592 25.76855,37.39551 25.76855,60.98389c0,20.61377 -5.04102,42.08691 -15.11719,64.41895c-10.08203,22.33203 -29.54687,51.59521 -58.40723,87.78271c-37.56738,47.41211 -64.93457,86.35352 -82.11328,116.8125c-13.51758,24.0498 -23.82422,49.24902 -30.9209,75.58594z');

    var openPath = new Path('M614 3449 c-98 -98 -174 -181 -174 -191 0 -10 58 -103 130 -207 72 -104 130 -193 130 -197 0 -16 -142 -369 -154 -381 -10 -11 -131 -37 -366 -79 -52 -9 -101 -21 -107 -26 -20 -16 -18 -507 1 -514 8 -3 118 -25 246 -49 127 -24 234 -45 237 -48 10 -11 153 -348 153 -363 0 -8 -33 -63 -74 -122 -158 -230 -196 -287 -196 -302 0 -17 339 -360 356 -360 6 0 103 63 215 140 112 77 209 140 216 140 7 0 77 -26 155 -59 79 -32 153 -62 166 -65 12 -4 26 -15 31 -25 6 -9 28 -118 51 -242 22 -123 45 -234 50 -247 10 -22 10 -22 259 -22 143 0 252 4 256 9 3 5 26 119 51 253 25 134 51 248 57 255 20 19 339 145 356 140 9 -2 105 -66 214 -141 108 -75 203 -136 210 -136 7 0 90 78 186 174 130 130 173 179 169 193 -3 10 -64 104 -137 209 -72 106 -131 200 -131 209 0 19 136 344 150 359 5 4 113 28 240 51 128 24 239 46 246 49 19 7 21 498 2 513 -7 6 -114 29 -237 51 -123 23 -230 47 -237 54 -13 13 -154 360 -154 379 0 11 84 138 192 291 37 53 68 105 68 115 0 17 -337 363 -353 363 -4 0 -93 -58 -198 -130 -105 -71 -197 -130 -204 -130 -7 0 -52 21 -100 46 -101 54 -97 56 -142 -56 -17 -41 -89 -218 -162 -392 -72 -175 -131 -324 -131 -332 0 -8 31 -37 70 -66 170 -126 240 -257 240 -451 0 -152 -57 -281 -170 -382 -213 -192 -536 -178 -730 30 -158 168 -189 398 -83 607 31 62 109 145 191 203 34 25 62 51 62 58 0 7 -35 97 -78 201 -44 104 -118 285 -167 402 -48 117 -92 215 -96 218 -5 3 -48 -15 -97 -40 -48 -26 -94 -46 -101 -46 -7 0 -90 53 -185 119 -95 65 -183 124 -195 131 -22 11 -34 1 -197 -161z')

    var boids = [];
    var groupTogether = false;

    // Add the boids:
    for (var i = 0; i < 21; i++) {
        var position = Point.random() * view.size;
        boids.push(new Boid(position, 1, 0.05));
    }


    function onFrame(event) {
        for (var i = 0, l = boids.length; i < l; i++) {
            if (groupTogether) {
                var length = ((i + event.count / 120) % l) / l * openPath.length;

                var point = openPath.getPointAt(length);
                if (point)
                    boids[i].arrive(point);
            }
            boids[i].run(boids);
        }
    }

    // Reposition the heart path whenever the window is resized:
    function onResize(event) {
        openPath.fitBounds(view.bounds);
        openPath.scale(0.8);
    }

    function onMouseDown(event) {
        groupTogether = !groupTogether;
    }

    function onKeyDown(event) {
        if (event.key == 'space') {
            var layer = project.activeLayer;
            layer.selected = !layer.selected;
            groupTogether = !groupTogether;
            return false;
        }
    }
</script>
</body>
</html>
