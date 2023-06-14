const express = require("express");
const router = express.Router();
const { login, register, details } = require("../controllers/auth.controller");
const {
  createGroup,
  joinGroup,
  getGroupByUserId,
  getGroupByGroupId,
  getGroupByCode,
  filterGroup,
  updateGroup,
  deleteMember,
  deleteGroup,
  updateLocation,
} = require("../controllers/group.controller");
const {
  createMood,
  getMoods,
  updateMoods,
} = require("../controllers/mood.controller");
const { getRecommendation } = require("../controllers/recommender.controller");
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
router.get("/filterGroup", verifyToken, filterGroup);

router.put("/groups/:groupId", verifyToken, updateGroup);
router.put("/groups/:groupId/location", verifyToken, updateLocation);

router.delete("/groups/:groupId/members/:userId", verifyToken, deleteMember);
router.delete("/groups/:groupId", verifyToken, deleteGroup);

// Routes for moods
router.post("/createMood", verifyToken, createMood);
router.get("/moods", verifyToken, getMoods);
router.put("/groups/:groupId/mood", verifyToken, updateMoods);

// Routes for recommendation
router.post("/groups/:groupId/recommend", verifyToken, getRecommendation);

module.exports = router;
