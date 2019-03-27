# Changelog

## [2.0.0] - 2019-04-01
### Added
- Added prefix `SE` to all base models (e.g. SEProvider, SEConnection, SEAccount, SETransaction, SEStage, SEAttempt)
- Added definition Consent (model SEConsent)
- Added `returnUrl` and `locale` to SEAttempt
### Changed
- Upgraded library to use Saltedge API v5
- Renamed all occurrences of definition "Login" to "Connection"
- Renamed all occurrences of definition "Token" to "Connect Session"
### Removed
- CreateTokenRequest model
  
## [1.3.10] - 2019-03-18
### Changed
- Refactored TLSFactory

## [1.3.5] - 2018-12-10
### Added
- Network logging option

## [1.3.4] - 2018-12-05
### Changed
- Android target version to SDK 28

## [1.3.2] - 2018-09-27
### Added
- Local maven repository

## [1.2] - 2018-07-18
### Added
- SSLSocketFactory
- HPKP protocol support

## [1.1] - 2018-02-01
### Changed
- Upgrade to Saltedge API v4
- Android target version up to SDK 27
- Replaces Android Asynchronous Http Client library with Retrofit library;

## [1.0] - 2016-02-16
Upgrade to Saltedge API v3