const express = require("express");
const router = express.Router();
const { login, register, details } = require("../controllers/auth.controller");
const {
  createGroup,
  joinGroup,
  getGroupByUserId,
  getGroupByGroupId,
  getGroupByCode,
  searchGroup,
  filterGroup,
  updateGroup,
  deleteMember,
  deleteGroup,
} = require("../controllers/group.controller");
const {
  createMood,
  getMoods,
  updateMoods,
} = require("../controllers/mood.controller");
const verifyToken = require("../middlewares/jwt.middlewares");

// Basic routes for auth
router.post("/register", register);

router.post("/login", login);
router.get("/me", verifyToken, details);

// Routes for groups
router.post("/groups", verifyToken, createGroup);
router.post("/groups/join", verifyToken, joinGroup);

router.get("/groups", verifyToken, getGroupByUserId);
router.get("/groups/:groupId", verifyToken, getGroupByGroupId);
router.get("/getGroupByCode", verifyToken, getGroupByCode);
router.get("/searchGroup", verifyToken, searchGroup);
router.get("/filterGroup", verifyToken, filterGroup);

router.put("/updateGroup", verifyToken, updateGroup);

router.delete("/deleteMember", verifyToken, deleteMember);
router.delete("/deleteGroup", verifyToken, deleteGroup);

// Routes for moods
router.post("/createMood", verifyToken, createMood);
router.get("/moods", verifyToken, getMoods);
router.put("/groups/:groupId/mood", verifyToken, updateMoods);

module.exports = router;
