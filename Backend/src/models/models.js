const { DataTypes, Model } = require("sequelize");
const sequelize = require("../configs/db.config.js");

const User = sequelize.define(
  "User",
  {
    id: {
      type: DataTypes.INTEGER,
      autoIncrement: true,
      primaryKey: true,
      allowNull: false,
    },
    username: {
      type: DataTypes.STRING,
      allowNull: false,
    },
    password: {
      type: DataTypes.STRING,
      allowNull: false,
    },
    email: {
      type: DataTypes.STRING,
      allowNull: false,
    },
  },
  {
    sequelize,
    modelName: "User",
  }
);

const Group = sequelize.define(
  "Group",
  {
    id: {
      type: DataTypes.INTEGER,
      autoIncrement: true,
      primaryKey: true,
      allowNull: false,
    },
    adminId: {
      type: DataTypes.INTEGER,
      allowNull: true,
    },
    name: {
      type: DataTypes.STRING,
      allowNull: false,
    },
    code: {
      type: DataTypes.STRING,
      allowNull: true,
    },
    status: {
      type: DataTypes.STRING,
      allowNull: false,
      defaultValue: "Pending",
    },
    result: {
      type: DataTypes.STRING,
    },
  },
  {
    sequelize,
    modelName: "Group",
  }
);

const UserGroup = sequelize.define("UserGroup", {
  lat: {
    type: DataTypes.STRING,
  },
  lang: {
    type: DataTypes.STRING,
  },
  isAdmin: {
    type: DataTypes.BOOLEAN,
    allowNull: false,
    defaultValue: false,
  },
  // mood: {
  //   type: DataTypes.JSON,
  //   allowNull: true,
  // },
});

User.belongsToMany(Group, { through: UserGroup });
Group.belongsToMany(User, { through: UserGroup });

module.exports = {
  User,
  Group,
  UserGroup,
};
