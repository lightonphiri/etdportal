#!/usr/bin/perl

# check full repository for records with <metadata> and remove
# hussein suleman
# 21 march 2012

$|=1;

use DBI;
use XML::Parser;

$pa = new XML::Parser ();

my $dbh = DBI->connect ('dbi:mysql:dbuniona', 'dbuniona', 'dbuniona', {mysql_enable_utf8 => 1, RowCacheSize => 1, });

my $sth = $dbh->prepare ("select id,metadata from Archive");
$sth->{"mysql_use_result"} = 1;
my $res = $sth->execute;

my @records = ();
my $count = 0;
while ($row = $sth->fetchrow_hashref ())
{
#   print $counter++.": ".$row->{'ID'};
   
#   eval { $pa->parse ($row->{'MetaData'}.""); };
   $count++;
   if (($count % 1000) == 0)
   { print "."; }
   if ($row->{'metadata'} =~ /\< *metadata *\>(.*?)\< *\/ *metadata *\>/)
   {
      print "X";
      push (@records, [$row->{'id'}, $row->{'metadata'}, $1]);
   }
}

$sth->finish;

my $count2 = 0;
my $total2 = $#records + 1;
foreach my $record (@records)
{
   $count2++;
   print "$count2/$total2: ".$record->[0]."\n";
   my $statement = "update Archive set metadata=".$dbh->quote ($record->[2])." where id=".$dbh->quote ($record->[0]);
#   print $statement;
   my $res2 = $dbh->do ($statement);
}

#print $records[0]->[2];

$dbh->disconnect;
