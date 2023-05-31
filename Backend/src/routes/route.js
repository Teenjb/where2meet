const express = require("express");
const router = express.Router();
const { login, register, details } = require("../controllers/auth.controller");
const {
  createGroup,
  joinGroup,
  getGroupByUserId,
  getGroupByGroupId,
  searchGroup,
  updateGroup,
  deleteMember,
  deleteGroup,
} = require("../controllers/group.controller");
const verifyToken = require("../middlewares/jwt.middlewares");

// Basic routes for auth
router.post("/register", register);

router.get("/login", login);
router.get("/details", verifyToken, details);

// Routes for groups
router.post("/createGroup", verifyToken, createGroup);
router.post("/joinGroup", verifyToken, joinGroup);

router.get("/getGroupByUserId", verifyToken, getGroupByUserId);
router.get("/getGroupByGroupId", verifyToken, getGroupByGroupId);
router.get("/searchGroup", verifyToken, searchGroup);

router.put("/updateGroup", verifyToken, updateGroup);

router.delete("/deleteMember", verifyToken, deleteMember);
router.delete("/deleteGroup", verifyToken, deleteGroup);

module.exports = router;
