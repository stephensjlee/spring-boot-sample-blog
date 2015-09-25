create table blogEntries( 
	id int not null auto_increment, 
	name varchar(300) not null, 
	title varchar(300) not null,
	summary varchar(500) not null,
	htmlContent varchar(25000) not null, 
	markdownContent varchar(25000) not null, 
	postUrl varchar(100),
	backgroundUrl varchar(1000),
	addedDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  	updatedDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	primary key (id)
);