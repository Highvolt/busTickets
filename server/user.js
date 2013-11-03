module.exports=User;

function User(){
}

User.prototype.findUser=function(){
}

User.prototype.authUser=function(){


}

User.verifyKey=function(req,res,next){
    //TODO
    req.user=1;
    next();
}
