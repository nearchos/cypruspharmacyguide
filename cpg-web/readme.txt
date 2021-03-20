How to build

1. "/cpg-web": ng build --prod

2. delete all in "/docs" except CNAME

3. copy all from "/cpg-web/dist" to "/docs"

4. make a copy of "/docs/index.html" and name it "/docs/404.html"

5. ensure the file exists "docs/app-ads.txt" (copy from "/cpg-web/app-ads.txt" if needed)

How to update pharmacies

a. get the latest JSON from:

b. use the data to replace/update the file:  docs/assets/mock-data.json
