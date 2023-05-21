const express = require("express");
const bodyParser = require("body-parser");
const port = process.env.PORT || 3000;
const cors = require("cors");
const app = express();
require("dotenv").config();
const sequelize = require("./src/configs/db.config.js");
const User = require("./src/models/m_user.js");
const authRouter = require("./src/routes/auth.route.js");

const corsOptions = {
  origin: "*",
  Credentials: true,
  optionsSuccessStatus: 200,
};

sequelize.sync().then(() => {
    console.log("synced");
    });

app.use(cors(corsOptions));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

app.get("/w2m", (req, res) => {
  res.json({ message: "Welcome to the where2meet backend" });
});

app.use("/w2m", authRouter);

app.listen(port, () => {
  console.log("Server is running on port: " + port);
});
