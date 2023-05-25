const { User, Group, UserGroup } = require("../models/models.js");
const { checkRegex, hashPassword, comparePassword } = require("../utils/auth.util.js");
const jwt = require("jsonwebtoken");

async function login(req, res) {
  const { username, password } = req.query;
  // Check if username and password are valid
  try {
    console.log(username, password);
    await checkRegex(username, null, password, "login");
    // If valid, check if user exists
    const user = await User.findOne({
      where: {
        username: username,
      },
    });
    // If user exists, check if password matches
    if (!user) {
      throw new Error("User does not exist");
    }
    if (await comparePassword(password, user.password)) {
      const token = jwt.sign({ id: user.id }, process.env.JWT_SECRET);
      res.status(200).json({ message: "Login successful", token: token });
    } else {
      res.status(401).json({ message: "Login failed" });
    }
  } catch (error) {
    res.status(400).json({ message: error.message });
  }
}

async function register(req, res) {
  const { username, password, email } = req.body;
  // Check if username, email, and password are valid
  try {
    await checkRegex(username, email, password, "register");

    const existingUser = await User.findOne({
      where: {
        username: username,
      },
    });

    if (existingUser) {
      res.status(409).json({ message: "Username already exists" });
    } else {
      const hashedPassword = await hashPassword(password);

      const newUser = await User.create({
        username: username,
        password: hashedPassword,
        email: email,
      });

      res.status(201).json({ message: "User created" });
    }
  } catch (error) {
    res.status(400).json({ message: error.message });
  }
}

async function details(req, res) {
  const id = req.user;
  const user = await User.findOne({
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
