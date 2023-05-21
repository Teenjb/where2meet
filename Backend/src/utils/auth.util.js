const bcrypt = require("bcryptjs");

async function hashPassword(password) {
  console.log(password);
  const salt = await bcrypt.genSalt(10);
  const hash = await bcrypt.hash(password, salt);
  return hash;
}

async function comparePassword(password, hash) {
  const isMatch = await bcrypt.compare(password, hash);
  return isMatch;
}

module.exports = {
  hashPassword,
  comparePassword,
};
