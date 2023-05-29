const crypto = require("crypto");
const { UserGroup } = require("../models/models");

async function generateCode() {
  do {
    var code = crypto.randomBytes(3).toString("hex");
    UserGroup.findOne({ where: { code: code } });
  } while (UserGroup.code === code);
  return code;
}

module.exports = {
  generateCode,
};
