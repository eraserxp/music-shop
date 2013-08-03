//not complete yet. Will work on this later. 

drop table Item;
create table Item(
  upc int not null,
	title varchar(40) not null,
	type  varchar(20) not null,
	category varchar(20) not null,
	company varchar(40) not null,
	year char(10) not null,
	price int not null,
	stock int not null,
	PRIMARY KEY (upc));
	
	
drop table LeadSinger;
create table LeadSinger(
	upc int not null ,
	name varchar(40) not null,
	PRIMARY KEY (upc, name),
	FOREIGN KEY (upc) references Item, 
	on update cascade,
	on delete cascade);
	
	
drop table HasSong;
create table HasSong(
	upc int not null,
	title varchar(20) not null,
	PRIMARY KEY (upc, title),
	FOREIGN KEY (upc) references Item, 
	on update cascade,
	on delete cascade);
	
	
drop table Purchase;
create table Purchase(
	receiptID varchar(10) not null,
	Pdate varchar(20) not null,
	cid int ,
	cardN int ,
	expiryDate varchar(20) ,
	expectedDate varchar(20) ,
	deliveredDate varchar(20),
	PRIMARY KEY (receiptID));
	

drop table PurchaseItem;
create table PurchaseItem(
	receiptID varchar(10) not null ,
	upc int not null ,
	quantity int not null,
	PRIMARY KEY (receiptID, upc),
	FOREIGN KEY (upc) references Item,
	FOREIGN KEY (receiptID) references Purchase, 
	on update cascade,
	on delete cascade);
	

drop table Customer;
create table Customer(
	cid varchar(8) not null ,
	cpassword varchar(10) not null,
	cname varchar(40) not null,
	caddress varchar(40) not null,
	cphone int not null,
	PRIMARY KEY (cid));
	
	
drop table Return;
create table Return(
	retid varchar(10) not null,
	redate varchar(20) not null,
	receiptID varchar(10) not null,
	PRIMARY KEY (retid),
	FOREIGN KEY (receiptID) references Purchase, 
	on update cascade,
	on delete cascade);
	
	
drop table ReturnItem;
create table ReturnItem(
	retid varchar(10) not null ,
	upc int not null ,
	quantity int not null,
	PRIMARY KEY (retid, upc),
	FOREIGN KEY (upc) references Item,
	FOREIGN KEY (retID) references Return, 
	on update cascade,
	on delete cascade);

insert into Item
values(11111, 'The Key', 'CD',
'pop', 'EMI', '2013', 12, 100);

insert into LeadSinger
values(11111, 'Ethan Chen');	
	
insert into HasSong
values(11111, 'Farewell Saha');		
insert into HasSong
values(11111, 'The Wanderer');	
insert into HasSong
values(11111, 'So far So close');		

insert into Purchase
values('A123456789', '2013-08-01', null, 1234123412341234, '2014-01-01', null, null);	

insert into PurchaseItem
values('A123456789', 11111, 1);	


	
	
	
