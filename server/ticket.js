function Ticket(){
    this.generateUID();
}

Ticket.prototype.generateUID(){
    this.UID=Math.random().toString(36).substr(2, 5)+Math.random().toString(36).substr(2, 5)+Math.random().toString(36).substr(2, 5)+Math.random().toString(36).substr(2, 1);
}
