exports.testRoute = (req, res) => {
  try{
    res.status(200).json({
      message: 'this is the test route'
    })
  }catch(err){
    res.status(500).json({error: err.message});
  }
}

exports.echoRoute = (req, res) => {
  try{
    res.status(200).json({
      message: req.body.message || req.query.message || null
    })
  }catch(err){
    res.status(500).json({error: err.message});
  }
}