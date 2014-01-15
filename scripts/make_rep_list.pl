#!/usr/bin/perl

# convert OCLC list of archives to etdportal mysql insert statements
# hussein suleman
# 1 april 2011

open (my $orgfile, "ORGLIST.txt");
my @repos = <$orgfile>;
close ($orgfile);

foreach my $repo (@repos)
{
   chomp $repo;
   
   my @fields = split (" ", $repo);
   
   my $baseURL = $fields[0]; 
   my $set = $fields[3];
   my $id = $fields[4];
   
   if ($id =~ /(.*)_[0-9]+$/)
   {
      $id = $1;
   }
   
   if ($set eq 'null')
   { $set = ''; }
   
   if (! exists $b2i{$baseURL})
   { $b2i{$baseURL} = $id; }
   
   if (! exists $b2s{$baseURL})
   { $b2s{$baseURL} = $set; }
   else
   { $b2s{$baseURL} = $b2s{$baseURL}.','.$set; }
}

foreach my $bu (keys %b2i)
{
#   print "$bu $b2i{$bu} $b2s{$bu}\n";   
   print "INSERT into Repositories set ID=\'$b2i{$bu}\', name=\'$b2i{$bu}\', baseURL=\'$bu\', setSpec=\'$b2s{$bu}\';\n"
}

