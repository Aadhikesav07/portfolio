# Chatbot Setup Guide

## Current Implementation

The chatbot currently works **without any API keys** using a rule-based system. It responds to questions based on keyword matching and provides information about courses, exams, assignments, and certificates.

## Two Options

### Option 1: Rule-Based Chatbot (Current - No API Keys Needed) ✅

**Status:** Already working!

The chatbot uses intelligent keyword matching and provides contextual responses based on:
- Available courses in the system
- Common questions about exams, assignments, certificates
- Platform guidelines and information

**No configuration needed** - it works out of the box!

### Option 2: AI-Powered Chatbot (Requires API Key)

To enable AI-powered responses using OpenAI or Google Gemini:

#### Step 1: Get an API Key

**For OpenAI:**
1. Go to https://platform.openai.com/api-keys
2. Sign up or log in
3. Create a new API key
4. Copy the key (starts with `sk-...`)

**For Google Gemini:**
1. Go to https://makersuite.google.com/app/apikey
2. Create an API key
3. Copy the key

#### Step 2: Configure the Application

Edit `backend/src/main/resources/application.properties`:

```properties
# Enable AI chatbot
chatbot.ai.enabled=true

# Choose provider: openai or gemini
chatbot.ai.provider=openai

# Add your API key
chatbot.ai.api.key=sk-your-openai-api-key-here

# OpenAI URL (default, usually don't need to change)
chatbot.ai.api.url=https://api.openai.com/v1/chat/completions
```

#### Step 3: Restart the Backend

After adding the API key, restart your Spring Boot application.

## How It Works

### Rule-Based Mode (Default)
- ✅ No API keys needed
- ✅ No external dependencies
- ✅ Fast responses
- ✅ No costs
- ⚠️ Limited to predefined responses

### AI Mode (When Enabled)
- ✅ Natural language understanding
- ✅ Contextual responses
- ✅ Can answer complex questions
- ⚠️ Requires API key
- ⚠️ May incur costs (OpenAI charges per request)
- ⚠️ Requires internet connection

## Cost Information

**OpenAI Pricing (as of 2024):**
- GPT-3.5-turbo: ~$0.0015 per 1K tokens (input) + $0.002 per 1K tokens (output)
- Average conversation: ~$0.01-0.05 per interaction
- Free tier: $5 credit for new users

**Google Gemini:**
- Free tier available with limits
- Check current pricing at https://ai.google.dev/pricing

## Security Notes

⚠️ **Important:** Never commit your API key to version control!

1. Add `application.properties` to `.gitignore` if it contains secrets
2. Use environment variables for production:
   ```bash
   export CHATBOT_AI_API_KEY=sk-your-key-here
   ```
3. Or use Spring profiles for different environments

## Testing

1. **Test Rule-Based (Default):**
   - Start the backend
   - Go to Chatbot page in frontend
   - Ask: "What courses are available?"
   - Should get a response with course names

2. **Test AI Mode (If Enabled):**
   - Configure API key in `application.properties`
   - Restart backend
   - Ask: "Tell me about machine learning courses"
   - Should get a more natural, contextual response

## Troubleshooting

**If AI mode doesn't work:**
1. Check API key is correct
2. Verify `chatbot.ai.enabled=true`
3. Check internet connection
4. Review backend logs for errors
5. System will automatically fallback to rule-based if AI fails

**If you see errors:**
- Check the console logs
- Verify API key format (OpenAI keys start with `sk-`)
- Ensure you have credits/quota in your AI provider account

## Recommendation

For development/testing: **Use rule-based (default)** - it's free and works well for basic queries.

For production: **Consider AI mode** if you want more natural conversations, but monitor costs.

