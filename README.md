![Carey Development Logo](http://careydevelopment.us/img/branding/careydevelopment-logo-sm.png)

# Carey Development Ecosystem User Service

This is a Spring Boot application that's used with the "Building a CRM Application " series on the <a href="https://careydevelopment.us" target="_blank">Carey Development website</a>.

It's a microservice that handles requests related to users within an ecosystem.

Each branch within this repo is related to a distinct guide. The master branch holds the latest version of the application.

If you want to follow along with the series, just visit the URL that points to the <a href="https://careydevelopment.us/tag/careydevelopmentcrm" target="_blank">careydevelopmentcrm tag</a>. 

Remember, all guides are in reverse chronological order so if you want to start from the beginning, you'll need to go to the last page.

## You Need to Make Updates
Bad news: you can't just clone this source and run it right out of the box. You'll need to make some changes.

For example, you'll need to update the MongoDB connection string in `application.properties`.

You might like to make some other configuration changes as well (e.g., maximum upload file size).

If you're deploying to Kubernetes, you could also store those properties in an external config file as I describe <a href="https://careydevelopment.us/blog/spring-boot-and-kubernetes-how-to-use-an-external-json-configuration" target="_blank">here</a>.

## The UI
The Carey Development CRM <a href="https://github.com/careydevelopment/careydevelopmentcrm">source</a> uses this service.

## License
This code is under the [MIT License](https://github.com/careydevelopment/ecosystem-user-service/blob/master/LICENSE).