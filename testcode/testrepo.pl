#!/usr/bin/perl

use DBI;

binmode(STDOUT, ":utf8");

my $dbh = DBI->connect ("DBI:mysql:dbuniona", 'dbuniona', 'dbuniona');

my $sth = $dbh->prepare ("select * from Repositories");
$sth->execute();

while (my $row = $sth->fetchrow_hashref ())
{
   my $name = $row->{'name'};
   
#   $name =~ s/\x00([\x80-\xff])/sprintf ("%c", ord($1))/goe;
#   $name =~ s/([\x80-\xff])/sprintf ("%c", ord($1))/goe;
   
   print "$row->{'ID'} $name\n";
   
#   my $sth2 = $dbh->prepare ("update Repositories set name=\'$name\' where ID=\'$row->{'ID'}\'");
#   $sth2->execute();
#   $sth2->finish ();
   
}

$sth->finish ();
$dbh->disconnect ();

