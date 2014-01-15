#!/usr/bin/perl

# check full repository for XML parsing errors in records
# hussein suleman
# 9 march 2012

$|=1;

use DBI;
use XML::Parser;

$pa = new XML::Parser ();

my $dbh = DBI->connect ('dbi:mysql:dbuniona', 'dbuniona', 'dbuniona', {mysql_enable_utf8 => 1, RowCacheSize => 1, });

my $sth = $dbh->prepare ("select * from Archive where Deleted=0");
$sth->{"mysql_use_result"} = 1;
my $res = $sth->execute;

my $counter = 0;
my @ids = ();
while ($row = $sth->fetchrow_hashref ())
{
   $counter++;
#   print $counter++.": ".$row->{'ID'};
   
   eval { $pa->parse ($row->{'MetaData'}.""); };
   my $parse_result = $@;
   
   if ($parse_result ne '')
   {
#      print "----".substr ($row->{'MetaData'}, 0, 80);
      print "\n".$counter.": ".$row->{'ID'};
      print "  ********** PARSING ERROR **********";
      print "\n";
      
      push (@ids, $row->{'ID'});
      
#      my $res2 = $dbh->do ("insert into Archive_Errors select * from Archive where ID=\'".$row->{'ID'}."\'");      
#      $res2 = $dbh->do ("delete from Archive where ID=\'".$row->{'ID'}."\'");
   }
   
   if ($counter % 1000 == 0)
   {
      print ".";
   }
      
#   print "\n";
}

$sth->finish;

foreach my $id (@ids)
{
   print "moving $id\n";
   my $res2 = $dbh->do ("insert into Archive_Errors select * from Archive where ID=\'$id\'");      
   $res2 = $dbh->do ("delete from Archive where ID=\'$id\'");
}

$dbh->disconnect;

