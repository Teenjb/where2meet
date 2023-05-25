const auth = require("../services/auth.service");

async function login(req, res) {
  try {
    await auth.login(req, res);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
}

async function register(req, res) {
  try {
    await auth.register(req, res);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
}

async function details(req, res) {
  try {
    await auth.details(req, res);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
}



module.exports = {
  login,
  register,
  details,
};
