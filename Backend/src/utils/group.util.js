const crypto = require("crypto");
const { Group } = require("../models/models");

async function generateCode() {
  do {
    var code = crypto.randomBytes(3).toString("hex");
    Group.findOne({ where: { code: code } });
  } while (Group.code === code);
  return code;
}

module.exports = {
  generateCode,
};
