const midpoint = ([x1, y1], [x2, y2]) => [(x1 + x2) / 2, (y1 + y2) / 2];

function get_polygon_centroid(pts) {
  if (pts.length == 1) {
    return pts[0];
  } else if (pts.length == 2) {
    return middlePoint(pts[0].x, pts[0].y, pts[1].x, pts[1].y);
  } else {
    var first = pts[0],
      last = pts[pts.length - 1];
    if (first.x != last.x || first.y != last.y) pts.push(first);
    var twicearea = 0,
      x = 0,
      y = 0,
      nPts = pts.length,
      p1,
      p2,
      f;
    for (var i = 0, j = nPts - 1; i < nPts; j = i++) {
      p1 = pts[i];
      p2 = pts[j];
      f =
        (p1.y - first.y) * (p2.x - first.x) -
        (p2.y - first.y) * (p1.x - first.x);
      twicearea += f;
      x += (p1.x + p2.x - 2 * first.x) * f;
      y += (p1.y + p2.y - 2 * first.y) * f;
    }
    f = twicearea * 3;
    return { x: x / f + first.x, y: y / f + first.y };
  }
}

//-6.263310, 106.865636
//-6.265827, 106.877695
//-6.273079, 106.867181
var points = [
  { x: -6.26331, y: 106.865636 },
  { x: -6.265827, y: 106.877695 },
  //  {x:-6.273079, y: 106.867181},
];
// original get_polygon_centroid(points)
// results in { x: 77.99957948181007, y: 40.00065236005001 }
// console.log(get_polygon_centroid(points))
console.log(midpoint([points[0].x, points[0].y], [points[1].x, points[1].y]));
// result is { x: 78.0000644, y: 40.000901033333335 }
