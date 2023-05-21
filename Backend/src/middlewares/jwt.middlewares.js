const jwt = require("jsonwebtoken");

async function verifyToken(req, res, next) {
    let bearerToken = req.headers["authorization"];
    const token = bearerToken.split(" ")[1];
    console.log(token);
  
    if (!token) {
      return res.status(403).send({
        message: "No token provided!",
      });
    }
  
    jwt.verify(token, process.env.JWT_SECRET, (err, decoded) => {
      if (err) {
        return res.status(401).send({
          message: err.message,
        });
      }
      req.user = decoded.id;
      next();
    });
  }
  
module.exports = verifyToken;