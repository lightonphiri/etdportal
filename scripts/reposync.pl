#!/usr/bin/perl

use DBI;

binmode(STDOUT, ":utf8");

my $dbh = DBI->connect ("DBI:mysql:dbuniona", 'dbuniona', 'dbuniona');

my $sth = $dbh->prepare ("select * from Repositories");
$sth->execute();

my $repos= {};
while (my $row = $sth->fetchrow_hashref ())
{
   $repos->{$row->{'ID'}} = 1;
}

$sth->finish ();

my $sth2 = $dbh->prepare ("select distinct Source from Archive");
$sth2->execute();

my $repos2= {};
while (my $row2 = $sth2->fetchrow_hashref ())
{
   $repos2->{$row2->{'Source'}} = 1;
}

$sth2->finish ();

print "In repositories ...\n";
foreach my $r1 (keys %$repos)
{
   if (! exists $repos2->{$r1}) 
   { print "$r1 ===\n"; }
}
print "In archive ...\n";
foreach my $r2 (keys %$repos2)
{
   if (! exists $repos->{$r2}) 
   { print "$r2 ===\n"; }
}

$dbh->disconnect ();

