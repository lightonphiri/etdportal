#!/usr/bin/perl

# update name for sets
# hussein suleman
# 3 april 2011

open (my $setfile, "listsets.xml");
my @lines = <$setfile>;
close ($setfile);

my $data = join ('', @lines);

my @sets = split ('<set>', $data);
shift @sets;

foreach my $set (@sets)
{
   if ($set =~ /\<setSpec\>(.+)\<\/setSpec\>\<setName\>(.+)\<\/setName\>/)
   {
      my ($spec, $name) = ($1, $2);
      $name =~ s/'/\\\'/go;
      print "UPDATE Repositories set name=\'$name\' where ID=\'$spec\';\n";
#      print "$spec $name\n";
   }

#   print "$set\n";
}


#foreach my $bu (keys %b2i)
#{
#   print "$bu $b2i{$bu} $b2s{$bu}\n";   
#   print "INSERT into Repositories set ID=\'$b2i{$bu}\', name=\'$b2i{$bu}\', baseURL=\'$bu\', setSpec=\'$b2s{$bu}\';\n"
#}

