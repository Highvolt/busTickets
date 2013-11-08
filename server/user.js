module.exports=User;
var http=require('http');
var crypto = require("crypto");
var db=null;

var key='Cmov2013BusTickt';
var iv=null;

//http://www.random.org/strings/?num=2&len=20&digits=on&upperalpha=on&loweralpha=on&unique=on&format=plain&rnd=new

function User(){
 }

User.generateIVandKEY=function(){
    /*http.get("http://www.random.org/strings/?num=2&len=20&digits=on&upperalpha=on&loweralpha=on&unique=on&format=plain&rnd=new", function(res) {
        console.log("Got response: " + res.statusCode);
        //console.log("Got response: "+res.body);
        res.on("data", function(chunk) {
            //console.log("BODY: " + chunk);
            var values=(new String(chunk)).split('\n');
            //console.log('values '+JSON.stringify(values));
            if(values.length>=2){
                iv=values[1]+String.fromCharCode(Math.floor(Math.random()*100))+String.fromCharCode(Math.floor(Math.random()*100))+String.fromCharCode(Math.floor(Math.random()*100))+values[0];
                console.log(iv);
            }
        });
    }).on('error', function(e) {
        console.log("Got error: " + e.message);
    });*/
   iv='';
   for(var i=0; i<16; i++){
    iv+=String.fromCharCode(30+Math.floor(Math.random()*49));
   }
   iv= new Buffer(iv);
    key= new Buffer(key);

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
    try{
        var user=new String(User.decodeToken(req.body.key)).split('|');
        console.log(user);
        if(user.length!=2){
            throw new Error();
        }
    }catch(e){
        console.log('invalid token');
        res.status(403).send(JSON.stringify({'msg':'invalid key'}));
        return;
    }
    db.get("Select id,devID as dev, (Select count(*) from Conductor where Conductor.id=user.id) as conductor,"+
           "(Select count(*) from Ticket where type=1 and userid=User.id and useDate is NULL) as t1,"+
           "(Select count(*) from Ticket where type=2 and userid=User.id and useDate is NULL) as t2,"+
           " (Select count(*) from Ticket where type=3 and userid=User.id and useDate is NULL) as t3 from User where username=? and last_login=?",user[0],user[1],function(err,row){
        if(err | row==null){
            if(err)
                console.log(JSON.stringify(err));
            res.status(403).send(JSON.stringify({'msg':'invalid key'}));
            return;
        }else{
            if(row.conductor>0){
                console.log('We got a conductor on our systems');
            }
             req.user=row;
             next();

        }
    });
}

User.register=function(username,password,devID,number,csc,expire,next){
    if(db){
        db.run("Insert into User (username,password,devID) values (?,?,?)",username,password,devID,function(err){
            if(err){
                if(err.errno==19){
                    next(-2);
                }else{
                    next(err);
                }
            }else{
                db.run("Insert into CreditCard (number,csc,expire,userid) values (?,?,?,(Select id from User where rowid=?));",number,csc,expire,this.lastID,function(err){
                    if(err){
                        next(err);
                    }else{
                        next({});

                    }
                });
            }
        });
    }else{
        next(-1);
        return;
    }
}




User.decodeToken=function(token){
    var descipher=crypto.createDecipheriv("aes128", key, iv);
    descipher.setAutoPadding(true);
    var des=descipher.update(token,'base64','utf-8');
    return des+descipher.final('utf-8');

}

User.generateToken=function(username){
    var cipher = crypto.createCipheriv("aes128", key, iv);
    cipher.setAutoPadding(true);
    var time=(new Date()).getTime()/1000|0;
    var token=cipher.update(''+username+'|'+time,'utf-8','base64');
    token+= cipher.final('base64');

    return {'time':time,'token':token};
}

User.login=function(username,password,devID,next){
 if(db){
        db.get("Select * from User where username=? and password=? and devID=?;",username,password,devID,function(err,row){
            if(err){
                next(err);
            }else{
                if(row){
                    var tokenData=User.generateToken(row.username);
                    console.log(JSON.stringify(tokenData));
                    db.run("Update User set token=?,last_login=? where id=?;",tokenData.token,tokenData.time,row.id,function(err){
                        if(err){
                            next(err);
                        }else{
                            next(tokenData.token);
                        }
                    });
                }else{
                    next(-2);
                }
            }
        });
    }else{
        next(-1);
        return;
    }

}
