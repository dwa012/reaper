class Api::V1::ApiController < ApplicationController

  skip_before_filter :verify_authenticity_token

     def get_album
    default_includes = %w(AlbumId ArtistId Title)
    base_time = Time.parse('1000-01-01').utc.iso8601

    limit = params[:limit] ? params[:limit] : nil
    offset = params[:offset] ? params[:offset] : 0
    version = (params[:version] and params[:version] != "") ? Time.parse(params[:version]).utc.iso8601 : base_time
    includes = params[:includes] ? params[:includes].split(/,/) & default_includes : default_includes

    current_version = Album.order('updated_at desc').first.updated_at.utc.iso8601

    where_statement = version != base_time ? "updated_at > '#{version}'" : nil
    @items = current_version != version ? Album.where(where_statement).select(includes).offset(offset).limit(limit) : {}

    data = Hash.new
    data['errors'] = []
    data['count'] = @items.count
    data['version'] = current_version
    data['items'] = @items

    render :json => data
  end

  def post_album

    @errors = []
    @messages = []

    @album = Album.new(params[:album])

    if @album.save
      @messages << 'Item added'
    else
      @errors = @album.errors.full_messages
    end

    data = Hash.new
    data[:errors]   = @errors
    data[:messages] = @messages

    render :json => data
  end

  def get_artist
    default_includes = %w(ArtistId Name)
    base_time = Time.parse('1000-01-01').utc.iso8601

    limit = params[:limit] ? params[:limit] : nil
    offset = params[:offset] ? params[:offset] : 0
    version = (params[:version] and params[:version] != "0") ? Time.parse(params[:version]).utc.iso8601 : base_time
    includes = params[:includes] ? params[:includes].split(/,/) & default_includes : default_includes

    current_version = Artist.order('updated_at desc').first.updated_at.utc.iso8601

    where_statement = version != base_time ? "updated_at > '#{version}'" : nil
    @items = current_version != version ? Artist.where(where_statement).select(includes).offset(offset).limit(limit) : {}

    data = Hash.new
    data['errors'] = []
    data['count'] = @items.count
    data['version'] = current_version
    data['items'] = @items

    render :json => data
  end

  def post_artist

    @errors = []
    @messages = []

    @artist = Artist.new(params[:artist])

    if @artist.save
      @messages << 'Item added'
    else
      @errors = @artist.errors.full_messages
    end

    data = Hash.new
    data[:errors]   = @errors
    data[:messages] = @messages

    render :json => data
  end

  def get_customer
    default_includes = %w(Address City Company Country CustomerId Email Fax FirstName LastName Phone PostalCode State SupportRepId)
    base_time = Time.parse('1000-01-01').utc.iso8601

    limit = params[:limit] ? params[:limit] : nil
    offset = params[:offset] ? params[:offset] : 0
    version = (params[:version] and params[:version] != "0") ? Time.parse(params[:version]).utc.iso8601 : base_time
    includes = params[:includes] ? params[:includes].split(/,/) & default_includes : default_includes

    current_version = Customer.order('updated_at desc').first.updated_at.utc.iso8601

    where_statement = version != base_time ? "updated_at > '#{version}'" : nil
    @items = current_version != version ? Customer.where(where_statement).select(includes).offset(offset).limit(limit) : {}

    data = Hash.new
    data['errors'] = []
    data['count'] = @items.count
    data['version'] = current_version
    data['items'] = @items

    render :json => data
  end

  def post_customer

    @errors = []
    @messages = []

    @customer = Customer.new(params[:customer])

    if @customer.save
      @messages << 'Item added'
    else
      @errors = @customer.errors.full_messages
    end

    data = Hash.new
    data[:errors]   = @errors
    data[:messages] = @messages

    render :json => data
  end

  def get_employee
    default_includes = %w(Address BirthDate City Country Email EmployeeId Fax FirstName HireDate LastName Phone PostalCode ReportsTo State Title)
    base_time = Time.parse('1000-01-01').utc.iso8601

    limit = params[:limit] ? params[:limit] : nil
    offset = params[:offset] ? params[:offset] : 0
    version = (params[:version] and params[:version] != "0") ? Time.parse(params[:version]).utc.iso8601 : base_time
    includes = params[:includes] ? params[:includes].split(/,/) & default_includes : default_includes

    current_version = Employee.order('updated_at desc').first.updated_at.utc.iso8601

    where_statement = version != base_time ? "updated_at > '#{version}'" : nil
    @items = current_version != version ? Employee.where(where_statement).select(includes).offset(offset).limit(limit) : {}

    data = Hash.new
    data['errors'] = []
    data['count'] = @items.count
    data['version'] = current_version
    data['items'] = @items

    render :json => data
  end

  def post_employee

    @errors = []
    @messages = []

    @employee = Employee.new(params[:employee])

    if @employee.save
      @messages << 'Item added'
    else
      @errors = @employee.errors.full_messages
    end

    data = Hash.new
    data[:errors]   = @errors
    data[:messages] = @messages

    render :json => data
  end

  def get_genre
    default_includes = %w(GenreId Name)
    base_time = Time.parse('1000-01-01').utc.iso8601

    limit = params[:limit] ? params[:limit] : nil
    offset = params[:offset] ? params[:offset] : 0
    version = (params[:version] and params[:version] != "0") ? Time.parse(params[:version]).utc.iso8601 : base_time
    includes = params[:includes] ? params[:includes].split(/,/) & default_includes : default_includes

    current_version = Genre.order('updated_at desc').first.updated_at.utc.iso8601

    where_statement = version != base_time ? "updated_at > '#{version}'" : nil
    @items = current_version != version ? Genre.where(where_statement).select(includes).offset(offset).limit(limit) : {}

    data = Hash.new
    data['errors'] = []
    data['count'] = @items.count
    data['version'] = current_version
    data['items'] = @items

    render :json => data
  end

  def post_genre

    @errors = []
    @messages = []

    @genre = Genre.new(params[:genre])

    if @genre.save
      @messages << 'Item added'
    else
      @errors = @genre.errors.full_messages
    end

    data = Hash.new
    data[:errors]   = @errors
    data[:messages] = @messages

    render :json => data
  end

  def get_invoice_line
    default_includes = %w(InvoiceId InvoiceLineId Quantity TrackId UnitPrice)
    base_time = Time.parse('1000-01-01').utc.iso8601

    limit = params[:limit] ? params[:limit] : nil
    offset = params[:offset] ? params[:offset] : 0
    version = (params[:version] and params[:version] != "0") ? Time.parse(params[:version]).utc.iso8601 : base_time
    includes = params[:includes] ? params[:includes].split(/,/) & default_includes : default_includes

    current_version = InvoiceLine.order('updated_at desc').first.updated_at.utc.iso8601

    where_statement = version != base_time ? "updated_at > '#{version}'" : nil
    @items = current_version != version ? InvoiceLine.where(where_statement).select(includes).offset(offset).limit(limit) : {}

    data = Hash.new
    data['errors'] = []
    data['count'] = @items.count
    data['version'] = current_version
    data['items'] = @items

    render :json => data
  end

  def post_invoice_line

    @errors = []
    @messages = []

    @invoice_line = InvoiceLine.new(params[:invoice_line])

    if @invoice_line.save
      @messages << 'Item added'
    else
      @errors = @invoice_line.errors.full_messages
    end

    data = Hash.new
    data[:errors]   = @errors
    data[:messages] = @messages

    render :json => data
  end

  def get_invoice
    default_includes = %w(BillingAddress BillingCity BillingCountry BillingPostalCode BillingState CustomerId InvoiceDate InvoiceId Total)
    base_time = Time.parse('1000-01-01').utc.iso8601

    limit = params[:limit] ? params[:limit] : nil
    offset = params[:offset] ? params[:offset] : 0
    version = (params[:version] and params[:version] != "0") ? Time.parse(params[:version]).utc.iso8601 : base_time
    includes = params[:includes] ? params[:includes].split(/,/) & default_includes : default_includes

    current_version = Invoice.order('updated_at desc').first.updated_at.utc.iso8601

    where_statement = version != base_time ? "updated_at > '#{version}'" : nil
    @items = current_version != version ? Invoice.where(where_statement).select(includes).offset(offset).limit(limit) : {}

    data = Hash.new
    data['errors'] = []
    data['count'] = @items.count
    data['version'] = current_version
    data['items'] = @items

    render :json => data
  end

  def post_invoice

    @errors = []
    @messages = []

    @invoice = Invoice.new(params[:invoice])

    if @invoice.save
      @messages << 'Item added'
    else
      @errors = @invoice.errors.full_messages
    end

    data = Hash.new
    data[:errors]   = @errors
    data[:messages] = @messages

    render :json => data
  end

  def get_media_type
    default_includes = %w(MediaTypeId Name)
    base_time = Time.parse('1000-01-01').utc.iso8601

    limit = params[:limit] ? params[:limit] : nil
    offset = params[:offset] ? params[:offset] : 0
    version = (params[:version] and params[:version] != "0") ? Time.parse(params[:version]).utc.iso8601 : base_time
    includes = params[:includes] ? params[:includes].split(/,/) & default_includes : default_includes

    current_version = MediaType.order('updated_at desc').first.updated_at.utc.iso8601

    where_statement = version != base_time ? "updated_at > '#{version}'" : nil
    @items = current_version != version ? MediaType.where(where_statement).select(includes).offset(offset).limit(limit) : {}

    data = Hash.new
    data['errors'] = []
    data['count'] = @items.count
    data['version'] = current_version
    data['items'] = @items

    render :json => data
  end

  def post_media_type

    @errors = []
    @messages = []

    @media_type = MediaType.new(params[:media_type])

    if @media_type.save
      @messages << 'Item added'
    else
      @errors = @media_type.errors.full_messages
    end

    data = Hash.new
    data[:errors]   = @errors
    data[:messages] = @messages

    render :json => data
  end

  def get_playlist
    default_includes = %w(Name PlaylistId)
    base_time = Time.parse('1000-01-01').utc.iso8601

    limit = params[:limit] ? params[:limit] : nil
    offset = params[:offset] ? params[:offset] : 0
    version = (params[:version] and params[:version] != "0") ? Time.parse(params[:version]).utc.iso8601 : base_time
    includes = params[:includes] ? params[:includes].split(/,/) & default_includes : default_includes

    current_version = Playlist.order('updated_at desc').first.updated_at.utc.iso8601

    where_statement = version != base_time ? "updated_at > '#{version}'" : nil
    @items = current_version != version ? Playlist.where(where_statement).select(includes).offset(offset).limit(limit) : {}

    data = Hash.new
    data['errors'] = []
    data['count'] = @items.count
    data['version'] = current_version
    data['items'] = @items

    render :json => data
  end

  def post_playlist

    @errors = []
    @messages = []

    @playlist = Playlist.new(params[:playlist])

    if @playlist.save
      @messages << 'Item added'
    else
      @errors = @playlist.errors.full_messages
    end

    data = Hash.new
    data[:errors]   = @errors
    data[:messages] = @messages

    render :json => data
  end

  def get_track
    default_includes = %w(AlbumId Bytes Composer GenreId MediaTypeId Milliseconds Name TrackId UnitPrice)
    base_time = Time.parse('1000-01-01').utc.iso8601

    limit = params[:limit] ? params[:limit] : nil
    offset = params[:offset] ? params[:offset] : 0
    version = (params[:version] and params[:version] != "0") ? Time.parse(params[:version]).utc.iso8601 : base_time
    includes = params[:includes] ? params[:includes].split(/,/) & default_includes : default_includes

    current_version = Track.order('updated_at desc').first.updated_at.utc.iso8601

    where_statement = version != base_time ? "updated_at > '#{version}'" : nil
    @items = current_version != version ? Track.where(where_statement).select(includes).offset(offset).limit(limit) : {}

    data = Hash.new
    data['errors'] = []
    data['count'] = @items.count
    data['version'] = current_version
    data['items'] = @items

    render :json => data
  end

  def post_track

    @errors = []
    @messages = []

    @track = Track.new(params[:track])

    if @track.save
      @messages << 'Item added'
    else
      @errors = @track.errors.full_messages
    end

    data = Hash.new
    data[:errors]   = @errors
    data[:messages] = @messages

    render :json => data
  end

end
