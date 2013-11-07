module.exports=Ticket;
var crypto=require('crypto');
var fs=require('fs');
var privKey=fs.readFileSync('./keys/rsa_priv.pem');
var db=null;

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

Ticket.createAndSign=function(user,type){
    var sign=crypto.createSign('RSA-SHA1');
    var time=new Date().getTime();
    sign.update(''+user+'-'+type+'-'+time);
    //add user deviD
    return {'user': user.id,'device':user.dev,'type':type,'time':new Date().getTime()/1000|0,'signature':sign.sign(privKey,'base64')};
}
