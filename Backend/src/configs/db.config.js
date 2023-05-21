const { Sequelize } = require("sequelize");

const sequelize = new Sequelize(
  process.env.PG_DATABASE,
  process.env.PG_USER,
  process.env.PG_PASSWORD,
  {
    dialect: "postgres",
    host: process.env.PG_HOST,
  }
);

sequelize
  .authenticate()
  .then(() => {
    console.log("Connected to the w2m database");
  })
  .catch((err) => {
    console.log(err);
  });

module.exports = sequelize;
