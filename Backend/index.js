const express = require("express");
const bodyParser = require("body-parser");
const port = process.env.PORT || 3000;
const cors = require("cors");
const app = express();
require("dotenv").config();
const sequelize = require("./src/configs/db.config.js");
const authRouter = require("./src/routes/auth.route.js");
const { logger, initLogCorrelation } = require("./src/utils/logging");
const { fetchProjectId } = require("./src/utils/metadata");

const corsOptions = {
  origin: "*",
  Credentials: true,
  optionsSuccessStatus: 200,
};

async function logging() {
  let project = process.env.GOOGLE_CLOUD_PROJECT;
  if (!project) {
    try {
      project = await fetchProjectId();
    } catch (err) {
      logger.warn("Could not fetch Project Id for tracing.");
    }
  }
  // Initialize request-based logger with project Id
  initLogCorrelation(project);
}

logging();

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
