#!/usr/bin/perl

# make urls go to oclc for empty sets
# hussein suleman
# 3 april 2011

open (my $countfile, "counts");
my @countlines = <$countfile>;
close ($countfile);
open (my $repofile, "repos");
my @repolines = <$repofile>;
close ($repofile);

shift @countlines;
shift @repolines;

my %fullrepos;

foreach my $count (@countlines)
{
   chomp $count;
   my @data = split ("\t", $count);
   $fullrepos{$data[0]} = 1; 
}

foreach my $repo (@repolines)
{
   chomp $repo;
   my @data = split ("\t", $repo);
   if (! exists $fullrepos{$data[0]})
   {
      print "UPDATE Repositories set baseURL=\'http://alcme.oclc.org/ndltd/servlet/OAIHandler\',setSpec=\'$data[0]\',dateFrom=\'\' where ID=\'$data[0]\';\n";
   }
}

#foreach my $set (@sets)
#{
#   if ($set =~ /\<setSpec\>(.+)\<\/setSpec\>\<setName\>(.+)\<\/setName\>/)
#   {
#      my ($spec, $name) = ($1, $2);
#      $name =~ s/'/\\\'/go;
#      print "UPDATE Repositories set name=\'$name\' where ID=\'$spec\';\n";
#      print "$spec $name\n";
#   }

#   print "$set\n";
#}


#foreach my $bu (keys %b2i)
#{
#   print "$bu $b2i{$bu} $b2s{$bu}\n";   
#   print "INSERT into Repositories set ID=\'$b2i{$bu}\', name=\'$b2i{$bu}\', baseURL=\'$bu\', setSpec=\'$b2s{$bu}\';\n"
#}

