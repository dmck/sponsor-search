README on Data
==============

The data for this project was obtained from govtrack.us whose
documentation is accessible at: https://www.govtrack.us/developers/data
The data is omitted from the project’s repository due to the size of the
files (approximately 50 megabytes per session). This README.md explains
how to setup the data for processing by this project.

There are two types of data which this project requires: bills, and
people.

Bills were obtained using rsync on Linux according to the instructions
at: <https://www.govtrack.us/developers/rsync> and specifically the
command:

    rsync -avz --delete --delete-excluded govtrack.us::govtrackdata/us/112/bills .

This produces approximately 8,000 .xml files (a.k.a. the bills) for the
112th session, within a folder named “bills/”. This folder should be
renamed with the session number (i.e. "112") and copied into "dat/".
Where the available years will be, for example:

    dat/112/h1.xml
    dat/112/h2.xml
    ...
    dat/113/h1.xml
    dat/113/h2.xml

People are determined by the project by combining
"legislators-current.csv" and "legislators-historic.csv" which are
available: <https://www.govtrack.us/data/congress-legislators/> These
files should be placed into:

    dat/people/legislators-current.csv
    dat/people/legislators-historic.csv
