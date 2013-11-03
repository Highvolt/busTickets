module.exports=User;

var db=null;



function User(){
 }

User.prototype.findUser=function(){
}

User.prototype.authUser=function(){


}

User.setDB=function(dbc){
    db=dbc;
}

User.verifyKey=function(req,res,next){
    //TODO
    req.user={'id':1,'dev':'abc'};
    next();
}


