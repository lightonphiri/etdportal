#!/usr/bin/perl

# update count table
# hussein suleman
# 9 march 2012

$|=1;

use DBI;
use XML::Parser;

$pa = new XML::Parser ();

my $dbh = DBI->connect ('dbi:mysql:dbuniona', 'dbuniona', 'dbuniona', {mysql_enable_utf8 => 1, RowCacheSize => 1, });

my $sth = $dbh->prepare ("select distinct Source from Archive");
$sth->{"mysql_use_result"} = 1;
my $res = $sth->execute;

my @sets = ();
while ($row = $sth->fetchrow_hashref ())
{
   push (@sets, $row->{'Source'});
   print ($row->{'Source'}.' ');
}   

$sth->finish;

print "\n";

foreach my $set (@sets)
{
   print "$set ";
   my $res2 = $dbh->do ("replace into Counter (setSpec, count) select Source,count(distinct ID) from Archive where Source=\'".$set."\'")
}

$dbh->disconnect;

print "\n";