module.exports=Ticket;
var crypto=require('crypto');
var fs=require('fs');
var privKey=fs.readFileSync('./keys/rsa_priv.pem').toString();
var pubKey=fs.readFileSync('./keys/rsa_pub.pem').toString();
var db=null;
var timelimit=[15,30,60];

function Ticket(){
}
Ticket.setDB=function(dbc){
    db=dbc;
}

Ticket.processRequest=function(req,res){
    var t1=Number(req.body.t1);
    var t2=Number(req.body.t2);
    var t3=Number(req.body.t3);
    var tickets=[];
    //Falta adicionar à base de dados e limitar a 10 por user comecar por verificar quantos tem cada user
    if(!isNaN(t1)){
        for(var i=0; i<t1 && i<10 && req.user.t1+i<10;i++){
            var t=Ticket.createAndSign(req.user,1);
            tickets.push(t);
            db.run("Insert into Ticket (ticketCode,userid,type,buyDate) values (?,?,?,?);",t.signature,t.user,t.type,t.time);
        }
    }
    if(!isNaN(t2)){
        for(var i=0; i<t2 && i<10 && req.user.t2+i<10;i++){
            var t=Ticket.createAndSign(req.user,2);
            tickets.push(t);
            db.run("Insert into Ticket (ticketCode,userid,type,buyDate) values (?,?,?,?);",t.signature,t.user,t.type,t.time);
        }
    }
     if(!isNaN(t3)){
        for(var i=0; i<t3 && i<10 && req.user.t3+i<10;i++){
            var t=Ticket.createAndSign(req.user,3);
            tickets.push(t);
            db.run("Insert into Ticket (ticketCode,userid,type,buyDate) values (?,?,?,?);",t.signature,t.user,t.type,t.time);
        }
    }
    res.send(JSON.stringify(tickets));
}


Ticket.getAllValidTickets=function(req,res){
    console.log('get Tickets');
    if(req.user==null){
        res.status(403);
        return;
    }else{
        db.all("Select User.id as user,user.devID as device,ticket.type as type,ticket.buyDate as time,ticket.tijsrsasigncketCode as signature from User,Ticket "+
               "where user.id=ticket.userid and user.id=? and ticket.useDate is NULL",req.user.id,
        function(err,data){
            if(err){
                res.send(JSON.stringify(err));
            }else{
                res.send(JSON.stringify(data));
            }
        });
    }
}

Ticket.createAndSign=function(user,type){
    var sign=crypto.createSign('RSA-SHA256');
    //var sign=new KJUR.crypto.Signature({"alg": "SHA1withRSA"});
    //sign.init(privKey);
    var time=new Date().getTime();
    sign.update(''+user.id+'-'+type+'-'+time);
    //sign.updateString(''+user.id+'-'+type+'-'+time);
    //add user deviD
    //var signature=sign.sign();
    var signature=sign.sign(privKey,'base64');
    console.log(''+user.id+'-'+type+'-'+time);
    console.log('Key: '+signature);
    /*var verify=new KJUR.crypto.Signature({"alg": "SHA1withRSA"});
    verify.init(pubKey);
    verify.updateString(''+user+'-'+type+'-'+time);
    console.log('Verificacao: ' + verify.verify(signature));*/
    return {'user': user.id,'device':user.dev,'type':type,'time':time,'signature':signature/*(new Buffer(signature,'hex')).toString('base64')*//*sign.sign(privKey,'base64')*/};
}

Ticket.validate=function(req,res,next){
    if(req.bus){
        res.status(401).send('');
        next();
        return;
    }else{
         var verifySign=crypto.createVerify('RSA-SHA256');
        if(typeof req.body.ticket == "string"){
            req.body.ticket=JSON.parse(req.body.ticket);
        }
        verifySign.update(''+req.body.ticket.user+'-'+req.body.ticket.type+'-'+req.body.ticket.time);
        console.log(''+req.body.ticket.user+'-'+req.body.ticket.type+'-'+req.body.ticket.time);
        var descodified=verifySign.verify(pubKey,req.body.ticket.signature,'base64');
        if(descodified){
            var time=(new Date()).getTime();
            db.run("Update Ticket set useDate=?,useBus=? where userid=? and buyDate=? and type=? and useDate is NULL;",time,req.bus.id,req.body.ticket.user,req.body.ticket.time,req.body.ticket.type,function(err){
                if(err){
                    res.status(500).send(JSON.stringify(err));
                }else{
                    if(this.changes==0){
                        res.status(400).send(JSON.stringify({'valid':0,'reason':'Not found or used'}));
                    }else{
                        req.body.ticket.useDate=time;
                        res.send(JSON.stringify(req.body.ticket));
                    }
                }
            });
        }else{
            res.status(400).send(JSON.stringify({'valid':0,'reason':'fake','msg':'Fake Ticket'}));
        }
    }
}


Ticket.iSvalidateTicket=function(req,res,next){
    if(req.user==null){
        res.status(403).send('');
        next();
        return;
    }else{
        var verifySign=crypto.createVerify('RSA-SHA256');
        // var verify=new KJUR.crypto.Signature({"alg": "SHA1withRSA"});
         //verify.init(pubKey);

        if(typeof req.body.ticket == "string"){
            req.body.ticket=JSON.parse(req.body.ticket);
        }
        //verify.updateString(''+req.body.ticket.user+'-'+req.body.ticket.type+'-'+req.body.ticket.time);
        verifySign.update(''+req.body.ticket.user+'-'+req.body.ticket.type+'-'+req.body.ticket.time);
        console.log(''+req.body.ticket.user+'-'+req.body.ticket.type+'-'+req.body.ticket.time);
        //console.log((new Buffer(req.body.ticket.signature,'base64')).toString('hex'));
        //verifySign.update(req.body.ticket.signature);
        //var descodified=verify.verify((new Buffer(req.body.ticket.signature,'base64')).toString('hex'));
        var descodified=verifySign.verify(pubKey,req.body.ticket.signature,'base64');
        console.log(descodified);
        if(descodified){
            db.get("Select * from Ticket where userid=? and buyDate=? and type=?;",req.body.ticket.user,req.body.ticket.time,req.body.ticket.type,function(err,row){
                if(err){
                    res.status(500).send(JSON.stringify(err));
                }else{
                    if(row){
                        if(row.useDate){
                            var timeNow=(new Date()).getTime();
                            var timediff=timeNow-row.useDate;
                            if(timediff>=0 && (timediff/1000/60)<timelimit[row.type-1]){
                                res.send(JSON.stringify({'valid':1,'timeLeft':timelimit[row.type-1]-(timediff/1000/60)}));
                            }else{
                                res.send(JSON.stringify({'valid':0,'reason':'expired','timeLeft':timelimit[row.type-1]-(timediff/1000/60)}));
                            }
                            //res.send(JSON.stringify({'valid':1,''}))

                        }else{
                            res.send(JSON.stringify({'valid':0,'reason':'Not used'}));
                        }
                    }else{
                        res.send(JSON.stringify({'valid':0,'reason':'Not found'}));
                    }
                }
            });
        }else{
            res.status(400).send(JSON.stringify({'valid':0,'reason':'fake','msg':'Fake Ticket'}));
        }
    }
}
