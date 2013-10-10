# coding: utf-8
lib = File.expand_path('../lib', __FILE__)
$LOAD_PATH.unshift(lib) unless $LOAD_PATH.include?(lib)
require 'reaper/version'

Gem::Specification.new do |spec|
  spec.name          = "reaper"
  spec.version       = Reaper::VERSION
  spec.authors       = ["Daniel Ward"]
  spec.email         = ["dwa012@gmail.com "]
  spec.description   = %q{Creates an API controller and can also create iOS and/or Android code to allow a client to connect to your Rails server.}
  spec.summary       = %q{API and Mobile client code generation}
  spec.homepage      = "http://github.com/dwa012/reaper"
  spec.license       = "Apache 2.0"

  spec.files         = `git ls-files`.split($/)
  spec.executables   = spec.files.grep(%r{^bin/}) { |f| File.basename(f) }
  spec.test_files    = spec.files.grep(%r{^(test|spec|features)/})
  spec.require_paths = ["lib"]

  spec.add_development_dependency "bundler", "~> 1.3"
  spec.add_development_dependency "rake"
end
