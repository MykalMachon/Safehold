exports.echoRoute = (req, res) => {
  try {
    const recievedMessage = req.body.message || req.query.message;
    if (!recievedMessage) {
      throw new Error('No Message Provided to Echo.');
    }
    res.status(200).json({
      message: recievedMessage,
    });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};
