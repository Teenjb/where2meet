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
      type: DataTypes.ARRAY(DataTypes.JSON),
    },
    generatedAt: {
      type: DataTypes.DATE,
      allowNull: true,
    },
  },
  {
    sequelize,
    modelName: "Group",
  }
);

const UserGroup = sequelize.define("UserGroup", {
  id: {
    type: DataTypes.INTEGER,
    autoIncrement: true,
    primaryKey: true,
    allowNull: false,
  },
  lat: {
    type: DataTypes.STRING,
  },
  long: {
    type: DataTypes.STRING,
  },
});

const Mood = sequelize.define("Mood", {
  id: {
    type: DataTypes.INTEGER,
    autoIncrement: true,
    primaryKey: true,
    allowNull: false,
  },
  name: {
    type: DataTypes.STRING,
  },
  displayText: {
    type: DataTypes.STRING,
  },
},
{
  sequelize,
  modelName: "Mood",
  timestamps: false,
});

const UserGroupMood = sequelize.define("UserGroupMood");

//User and Group
Group.hasMany(UserGroup, { as: "users" });
UserGroup.belongsTo(Group);
User.hasMany(UserGroup);
UserGroup.belongsTo(User);
Group.belongsToMany(User, { through: UserGroup });
User.belongsToMany(Group, { through: UserGroup });

//User Group and Mood
UserGroup.hasMany(UserGroupMood);
UserGroupMood.belongsTo(UserGroup);
Mood.hasMany(UserGroupMood);
UserGroupMood.belongsTo(Mood);
UserGroup.belongsToMany(Mood, { through: UserGroupMood, as: "moods"});
Mood.belongsToMany(UserGroup, { through: UserGroupMood});


module.exports = {
  User,
  Group,
  UserGroup,
  Mood,
  UserGroupMood,
};
