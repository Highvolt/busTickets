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

User.register=function(username,password,devID,next){
    if(db){
        db.run("Insert into User (username,password,devID) values (?,?,?)",username,password,devID,function(err){
            if(err){
                if(err.errno==19){
                    next(-2);
                }else{
                    next(err);
                }
            }else{
                next(null);
            }
        });
    }else{
        next(-1);
        return;
    }
}
