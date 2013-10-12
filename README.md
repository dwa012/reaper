# Reaper

Tested with Ruby 2 and Rails 3.2.x

## Installation

Add this line to your application's Gemfile:

    gem 'reaper', :git => 'https://github.com/dwa012/reaper.git'

And then execute:

    $ bundle install


## Usage

Reaper is a set of tasks that help connect your Rails server to iOS and Android devices.
These task read the models in your rails applicaiton and create all the code need to connect to the server. Follow the simple guide for the platform you wish to target.

The tasks are scoped to the reaper namespace.

	rake reaper:TARGET_PLATFORM [MODEL[,MODEL]]

Each target can take a list of models if you wish to target a subset of the server models.

[Big Android BBQ 2013 - Slides](http://goo.gl/z92UPq)

### API Controller

When you create the Rails API controller, a routes file is generated. You can either copy the routes into your regular routes file or add the following to your `application.rb`.

```ruby
config.paths['config/routes'] << Rails.root.join('config/routes/api.rb')
```

This will make sure that if you regenerate the API controllr, you can get the updated routes file without any additional work.

The contoller will be placed in `{APP_ROOT}/app/controllers/api/v1/`.

### Go API Server (Experimental)

Run `rake reaper:go` in the root of the Rails applicaiton.
 
Navigate to the `{APP_ROOT}/generated_files/golang` directory. 

1. Open `{APP_ROOT}/generated_files/golang/src/reaper/db/db.go` and update the database information to correspond to the database you are using and the relavant connection information.
2. Open `{APP_ROOT}/generated_files/golang/src/reaper/server/server.go` and change the listening port if required.

To run the server: 

1. Navigate to `{APP_ROOT}/generated_files/golang`
2. Run `go get reaper/server`
3. Run `go install reaper/server`
4. Run `{APP_ROOT}/generated_files/golang/bin/server`

You can then go to the server address in the browser `http://**host**/api/v1/**Model**`.

### Android Code

Run `rake reaper:android` in the root of the Rails applicaiton.

1.  Copy `{APP_ROOT}/generated_files/mobile_app/android/com` into the source fodler of your Android project.
2. Open `com/github/dwa012/reaper/api/Api.java` and change the `BASE_API_URL` constnt to point to the server.

There is an example Android project provided that uses the Rails API server. To get the Albums from the example Rails server do something like the following:

```
AlbumAsync async = new AlbumAsync(this);
async.fetchItems(new AsyncListener<Album>() {
    @Override
    public void retrievalFinished(List<Album> items) {
        // add items to the adapter list
        // refresh the adapter
    }
});
```


You will need to add some dependencies to your app to support the Reaper code.

[Jackson by CodeHaus](http://jackson.codehaus.org/)

[Guava by Google](http://jackson.codehaus.org/)

Gradle

```
compile 'com.google.guava:guava:14.0.1'
compile 'com.fasterxml.jackson.core:jackson-databind:2.1.3'
compile 'com.fasterxml.jackson.core:jackson-annotations:2.1.2'
compile 'com.fasterxml.jackson.core:jackson-core:2.1.3'
```

Maven

```
<dependency>
  <groupId>com.fasterxml.jackson.core</groupId>
  <artifactId>jackson-annotations</artifactId>
  <version>2.1.3</version>
</dependency>

<dependency>
  <groupId>com.fasterxml.jackson.core</groupId>
  <artifactId>jackson-core</artifactId>
  <version>2.1.3</version>
</dependency>

<dependency>
  <groupId>com.fasterxml.jackson.core</groupId>
  <artifactId>jackson-databind</artifactId>
  <version>2.1.3</version>
</dependency>
```

### iOS Code

Run `rake reaper:ios` in the root of the Rails applicaiton.

1.  Copy `{APP_ROOT}/generated_files/mobile_app/ios/*` into the source directory of your iOS project.
2. Open `api/APIController.m` and change the `API_URL` macro to point to the server.

You will need to add some dependencies to your app to support the Reaper code.

[RestKit](https://github.com/RestKit/RestKit)

Add

```
pod 'RestKit'
```

To your Podfile

## Contributing

1. Fork it
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create new Pull Request

## Contributors

[Jorge Chao](https://github.com/jchao)
