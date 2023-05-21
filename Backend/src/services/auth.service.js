const m_user = require("../models/m_user.js");
const { hashPassword, comparePassword } = require("../utils/auth.util.js");
const jwt = require("jsonwebtoken");

async function login(req, res) {
  const { username, password } = req.body;
  const user = await m_user.findOne({
    where: {
      username: username,
    },
  });
  var token = jwt.sign({ id: user.id }, process.env.JWT_SECRET);
  if (!user) {
    throw new Error("User does not exist");
  }
  if (await comparePassword(password, user.password)) {
    res.status(200).json({ message: "Login successful", token: token });
  } else {
    res.status(401).json({ message: "Login failed" });
  }
}

async function register(req, res) {
  const { username, password, email } = req.body;
  const user = await m_user.findOne({
    where: {
      username: username,
    },
  });
  if (user) {
    res.status(409).json({ message: "Username already exists" });
  } else {
    const newUser = await m_user.create({
      username: username,
      password: await hashPassword(password),
      email: email,
    });
    res.status(201).json({ message: "User created" });
  }
}

async function details(req, res) {
  const id = req.user;
  const user = await m_user.findOne({
    where: {
      id: id,
    },
  });
  if (!user) {
    res.status(404).json({ message: "User not found" });
  } else {
    res.status(200).json({ message: "User found", user: user });
  }
}

module.exports = {
    login,
    register,
    details,
}
