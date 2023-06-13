const bcrypt = require("bcryptjs");

async function checkRegex(username, email, password, type) {
  const usernameRegex = /^[a-zA-Z0-9]+$/;
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$/;
  const errors = [];
// Regex tests
  if (username === null || !usernameRegex.test(username)) {
    errors.push("Invalid username");
  }
  if (password === null || !passwordRegex.test(password)) {
    errors.push("Invalid password");
  }
  if (type === "register" && (email === null || !emailRegex.test(email))) {
    errors.push("Invalid email");
  }
  // Uncomment this to check for errors in deployment
  // If any errors occurred, throw an error with the error messages
  if (errors.length > 0) {
    throw new Error(errors.join(", "));
  }
}

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
  checkRegex,
  hashPassword,
  comparePassword,
};
